package com.quang.smart_recipe.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.quang.smart_recipe.dto.request.LoginRequestDTO;
import com.quang.smart_recipe.dto.request.RegisterRequestDTO;
import com.quang.smart_recipe.dto.response.AuthResponseDTO;
import com.quang.smart_recipe.entity.PasswordResetToken;
import com.quang.smart_recipe.entity.User;
import com.quang.smart_recipe.exception.AppException;
import com.quang.smart_recipe.exception.ErrorCode;
import com.quang.smart_recipe.repository.PasswordResetTokenRepository;
import com.quang.smart_recipe.repository.UserRepository;
import com.quang.smart_recipe.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;

    @Value("${app.google.client-id}")
    private String googleClientId;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);

        return new AuthResponseDTO(token, saved.getId(), saved.getUsername(), saved.getRole());
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String token = jwtService.generateToken(user);

        return new AuthResponseDTO(token, user.getId(), user.getUsername(), user.getRole());
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
                    newUser.setUsername(email.split("@")[0] + "_" + new Random().nextInt(1000));
                    newUser.setPassword(passwordEncoder.encode(new Random().nextLong() + "")); // Random pass
                    newUser.setRole("USER");
                    newUser.setGoogleId(googleId);
                    newUser.setAvatarUrl(pictureUrl);
                    return userRepository.save(newUser);
                });

                String token = jwtService.generateToken(user);
                return new AuthResponseDTO(token, user.getId(), user.getUsername(), user.getRole());
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
        String otp = String.format("%06d", new Random().nextInt(1000000));

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
}