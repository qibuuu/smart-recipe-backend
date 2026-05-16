package com.quang.smart_recipe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendOtp(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("SmartFridge <no-reply@smartfridge.app>");
        message.setTo(to);
        message.setSubject("Mã OTP khôi phục mật khẩu - SmartFridge");
        message.setText(
                "Chào bạn,\n\n" +
                "Mã OTP để khôi phục mật khẩu của bạn là: " + otp + "\n" +
                "Mã này sẽ hết hạn sau 10 phút.\n\n" +
                "Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.\n\n" +
                "Trân trọng,\nSmartFridge Team"
        );
        mailSender.send(message);
    }

    public void sendRegistrationOtp(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("SmartFridge <no-reply@smartfridge.app>");
        message.setTo(to);
        message.setSubject("Xác nhận tài khoản SmartFridge - Mã OTP của bạn");
        message.setText(
                "Chào mừng bạn đến với SmartFridge! 🎉\n\n" +
                "Cảm ơn bạn đã đăng ký tài khoản. Để hoàn tất quá trình đăng ký, " +
                "vui lòng nhập mã OTP dưới đây:\n\n" +
                "━━━━━━━━━━━━━━━━━━━━\n" +
                "   Mã xác nhận: " + otp + "\n" +
                "━━━━━━━━━━━━━━━━━━━━\n\n" +
                "Mã này sẽ hết hạn sau 10 phút.\n\n" +
                "Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email.\n\n" +
                "Trân trọng,\nSmartFridge Team"
        );
        mailSender.send(message);
    }

    public void sendWelcomeEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("SmartFridge <no-reply@smartfridge.app>");
        message.setTo(to);
        message.setSubject("Chào mừng " + username + " đến với SmartFridge! 🍽️");
        message.setText(
                "Xin chào " + username + ",\n\n" +
                "Tài khoản của bạn đã được xác nhận thành công! 🎉\n\n" +
                "Thông tin tài khoản:\n" +
                "• Tên đăng nhập: " + username + "\n" +
                "• Email: " + to + "\n\n" +
                "Bạn có thể bắt đầu khám phá SmartFridge ngay bây giờ:\n" +
                "• 🍳 Khám phá hàng trăm công thức nấu ăn\n" +
                "• 🧊 Quản lý tủ lạnh thông minh\n" +
                "• 🛒 Lên kế hoạch mua sắm\n" +
                "• 📅 Lập thực đơn hàng tuần\n\n" +
                "Chúc bạn có những bữa ăn thật ngon miệng!\n\n" +
                "Trân trọng,\nSmartFridge Team"
        );
        mailSender.send(message);
    }
}
