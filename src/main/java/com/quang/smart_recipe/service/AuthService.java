package com.quang.smart_recipe.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.quang.smart_recipe.dto.request.LoginRequestDTO;
import com.quang.smart_recipe.dto.request.RegisterRequestDTO;
import com.quang.smart_recipe.dto.response.AuthResponseDTO;
import com.quang.smart_recipe.entity.EmailVerificationToken;
import com.quang.smart_recipe.entity.PasswordResetToken;
import com.quang.smart_recipe.entity.User;
import com.quang.smart_recipe.exception.AppException;
import com.quang.smart_recipe.exception.ErrorCode;
import com.quang.smart_recipe.repository.EmailVerificationTokenRepository;
import com.quang.smart_recipe.repository.PasswordResetTokenRepository;
import com.quang.smart_recipe.repository.UserRepository;
import com.quang.smart_recipe.security.JwtService;
import com.quang.smart_recipe.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.security.SecureRandom;
import com.quang.smart_recipe.security.EncryptionUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailVerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;
    private final SecurityUtils securityUtils;

    @Value("${app.google.client-id}")
    private String googleClientId;

    @Value("${app.jwt.secret}")
    private String aesSecretKey;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        String normalizedEmail = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // Save user as unverified
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setEmailVerified(false);
        userRepository.save(user);

        // Generate and send registration OTP
        sendEmailVerificationOtp(user, request.getPassword());
    }

    private void sendEmailVerificationOtp(User user, String rawPassword) {
        String otp = String.format("%06d", secureRandom.nextInt(1000000));
        verificationTokenRepository.deleteByUser(user);
        
        String encrypted = rawPassword != null ? EncryptionUtils.encrypt(rawPassword, aesSecretKey) : null;

        EmailVerificationToken token = EmailVerificationToken.builder()
                .token(otp)
                .user(user)
                .encryptedPassword(encrypted) // Securely store AES-256 encrypted password
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();
        verificationTokenRepository.save(token);
        mailService.sendRegistrationOtp(user.getEmail(), user.getUsername(), null, otp); // Only OTP in first email
    }

    @Transactional
    public AuthResponseDTO verifyEmail(String otp) {
        EmailVerificationToken verificationToken = verificationTokenRepository.findByToken(otp)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_OTP));

        if (verificationToken.isExpired()) {
            verificationTokenRepository.delete(verificationToken);
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        User user = verificationToken.getUser();
        String rawPassword = EncryptionUtils.decrypt(verificationToken.getEncryptedPassword(), aesSecretKey); // Decrypt on OTP validation

        user.setEmailVerified(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);

        // Send welcome email with the password
        mailService.sendWelcomeEmail(user.getEmail(), user.getUsername(), rawPassword);

        String jwt = jwtService.generateToken(user);
        return new AuthResponseDTO(jwt, user.getId(), user.getUsername(), user.getRole(), user.getEmail());
    }

    public void resendVerificationOtp(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.isEmailVerified()) return; // Already verified, no need to resend
        sendEmailVerificationOtp(user, null); // Password not available on resend (hashed)
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Block login if email not verified
        if (!user.isEmailVerified()) {
            throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        String token = jwtService.generateToken(user);
        return new AuthResponseDTO(token, user.getId(), user.getUsername(), user.getRole(), user.getEmail());
    }

    public AuthResponseDTO googleLogin(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail().toLowerCase();
                String name = (String) payload.get("name");
                String googleId = payload.getSubject();
                String pictureUrl = (String) payload.get("picture");

                User user = userRepository.findByEmail(email).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(email.split("@")[0] + "_" + secureRandom.nextInt(1000));
                    newUser.setPassword(passwordEncoder.encode(secureRandom.nextLong() + ""));
                    newUser.setRole("USER");
                    newUser.setGoogleId(googleId);
                    newUser.setAvatarUrl(pictureUrl);
                    newUser.setEmailVerified(true); // Google already verified the email
                    String rawPassword = secureRandom.nextLong() + "";
                    newUser.setPassword(passwordEncoder.encode(rawPassword));
                    User saved = userRepository.save(newUser);
                    // Send welcome email for new Google users
                    mailService.sendWelcomeEmail(saved.getEmail(), saved.getUsername(), rawPassword);
                    return saved;
                });

                // Ensure existing users are also marked verified
                if (!user.isEmailVerified()) {
                    user.setEmailVerified(true);
                    userRepository.save(user);
                }

                String token = jwtService.generateToken(user);
                return new AuthResponseDTO(token, user.getId(), user.getUsername(), user.getRole(), user.getEmail());
            } else {
                throw new AppException(ErrorCode.GOOGLE_LOGIN_FAILED);
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.GOOGLE_LOGIN_FAILED);
        }
    }

    @Transactional
    public void forgotPassword(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Generate 6-digit OTP
        String otp = String.format("%06d", secureRandom.nextInt(1000000));

        // Delete old token if exists
        tokenRepository.deleteByUser(user);

        // Save new token
        PasswordResetToken token = PasswordResetToken.builder()
                .token(otp)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();
        tokenRepository.save(token);

        // Send email
        mailService.sendOtp(email, otp);
    }

    @Transactional
    public void resetPassword(String otp, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(otp)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_OTP));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setGoogleId(null); // Clear googleId if they reset pass manually? Or keep it? Usually keep.
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        User user = securityUtils.getCurrentUser();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public AuthResponseDTO getMyInfo() {
        User user = securityUtils.getCurrentUser();
        return new AuthResponseDTO(null, user.getId(), user.getUsername(), user.getRole(), user.getEmail());
    }
}