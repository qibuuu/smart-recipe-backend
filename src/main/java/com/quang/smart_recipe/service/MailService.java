package com.quang.smart_recipe.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    private void sendHtmlEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("SmartFridge <no-reply@smartfridge.app>");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendOtp(String to, String otp) {
        String content = "<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; border: 1px solid #eee; padding: 20px; border-radius: 10px;'>" +
                "<h2 style='color: #f43f5e; text-align: center;'>SmartFridge</h2>" +
                "<h3>Mã OTP khôi phục mật khẩu</h3>" +
                "<p>Chào bạn,</p>" +
                "<p>Chúng tôi nhận được yêu cầu khôi phục mật khẩu của bạn. Vui lòng sử dụng mã OTP dưới đây:</p>" +
                "<div style='text-align: center; background: #fef2f2; padding: 20px; border-radius: 8px; margin: 20px 0;'>" +
                "<span style='font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #f43f5e;'>" + otp + "</span>" +
                "</div>" +
                "<p>Mã này sẽ hết hạn sau <b>10 phút</b>.</p>" +
                "<p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.</p>" +
                "<hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>" +
                "<p style='font-size: 12px; color: #888; text-align: center;'>SmartFridge Team &bull; Fresh Harvest Project</p>" +
                "</div></body></html>";
        sendHtmlEmail(to, "Mã OTP khôi phục mật khẩu - SmartFridge", content);
    }

    public void sendRegistrationOtp(String to, String username, String password, String otp) {
        String content = "<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; border: 1px solid #eee; padding: 20px; border-radius: 10px;'>" +
                "<div style='text-align: center; margin-bottom: 20px;'>" +
                "<h1 style='color: #396938; margin: 0;'>SmartFridge</h1>" +
                "<p style='color: #666; margin: 5px 0;'>Hương vị của sự tươi mới</p>" +
                "</div>" +
                "<h3>Xác nhận đăng ký tài khoản</h3>" +
                "<p>Chào mừng bạn đến với <b>SmartFridge!</b> 🎉</p>" +
                "<p>Vui lòng sử dụng mã xác nhận dưới đây để hoàn tất đăng ký:</p>" +
                "<div style='text-align: center; background: #f0fdf4; padding: 25px; border-radius: 12px; margin: 25px 0; border: 1px dashed #396938;'>" +
                "<span style='font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #396938;'>" + otp + "</span>" +
                "</div>" +
                "<p>Mã này sẽ hết hạn sau <b>10 phút</b>. Vui lòng không chia sẻ mã này với bất kỳ ai.</p>" +
                "<hr style='border: 0; border-top: 1px solid #eee; margin: 30px 0;'>" +
                "<p style='font-size: 12px; color: #888; text-align: center;'>Cảm ơn bạn đã tham gia cùng chúng tôi!<br>SmartFridge Team</p>" +
                "</div></body></html>";
        sendHtmlEmail(to, "Xác nhận tài khoản SmartFridge - Mã OTP của bạn", content);
    }

    public void sendWelcomeEmail(String to, String username, String password) {
        String passwordSection = (password != null) 
            ? "<p><b>Mật khẩu của bạn:</b> <code style='background: #f4f4f4; padding: 2px 5px; border-radius: 3px;'>" + password + "</code></p>"
            : "<p><i>Vì lý do bảo mật, mật khẩu của bạn không được hiển thị ở đây.</i></p>";

        String content = "<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; border: 1px solid #eee; padding: 20px; border-radius: 10px; background: #ffffff;'>" +
                "<div style='text-align: center; background: linear-gradient(135deg, #396938, #5a8d59); padding: 30px; border-radius: 8px 8px 0 0; color: white;'>" +
                "<h1 style='margin: 0;'>SmartFridge</h1>" +
                "<p style='margin: 5px 0; opacity: 0.9;'>Chào mừng thành viên mới!</p>" +
                "</div>" +
                "<div style='padding: 20px;'>" +
                "<h2>Xin chào " + username + "! 🎉</h2>" +
                "<p>Chúc mừng! Tài khoản SmartFridge của bạn đã được kích hoạt thành công.</p>" +
                "<div style='background: #f8fafc; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #396938;'>" +
                "<h4 style='margin-top: 0; color: #396938;'>Thông tin tài khoản:</h4>" +
                "<p style='margin: 5px 0;'><b>Tên đăng nhập:</b> " + username + "</p>" +
                "<p style='margin: 5px 0;'><b>Email:</b> " + to + "</p>" +
                passwordSection +
                "</div>" +
                "<h3>Bạn có thể làm gì với SmartFridge?</h3>" +
                "<ul style='list-style: none; padding-left: 0;'>" +
                "<li style='margin-bottom: 10px;'>🍳 <b>Khám phá:</b> Hàng trăm công thức nấu ăn dựa trên nguyên liệu sẵn có.</li>" +
                "<li style='margin-bottom: 10px;'>🧊 <b>Quản lý:</b> Theo dõi hạn sử dụng và số lượng thực phẩm trong tủ lạnh.</li>" +
                "<li style='margin-bottom: 10px;'>🛒 <b>Mua sắm:</b> Tự động tạo danh sách đi chợ thông minh.</li>" +
                "<li style='margin-bottom: 10px;'>📅 <b>Kế hoạch:</b> Lên thực đơn hàng ngày để tiết kiệm thời gian và chi phí.</li>" +
                "</ul>" +
                "<div style='text-align: center; margin-top: 30px;'>" +
                "<a href='http://localhost:5173' style='background: #396938; color: white; padding: 12px 30px; text-decoration: none; border-radius: 25px; font-weight: bold; display: inline-block;'>Bắt đầu ngay</a>" +
                "</div>" +
                "</div>" +
                "<hr style='border: 0; border-top: 1px solid #eee; margin: 30px 0;'>" +
                "<p style='font-size: 12px; color: #888; text-align: center;'>Email này được gửi tự động từ hệ thống SmartFridge.<br>&copy; 2026 SmartFridge Team</p>" +
                "</div></body></html>";
        sendHtmlEmail(to, "Chào mừng " + username + " đến với SmartFridge! 🍽️", content);
    }
}
