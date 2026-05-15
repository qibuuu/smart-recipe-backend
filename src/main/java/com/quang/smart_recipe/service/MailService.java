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
        message.setFrom("SmartRecipe <no-reply@smartrecipe.com>");
        message.setTo(to);
        message.setSubject("Mã OTP khôi phục mật khẩu - SmartRecipe");
        message.setText("Chào bạn,\n\n" +
                "Mã OTP để khôi phục mật khẩu của bạn là: " + otp + "\n" +
                "Mã này sẽ hết hạn sau 10 phút.\n\n" +
                "Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.\n\n" +
                "Trân trọng,\nSmartRecipe Team");
        mailSender.send(message);
    }
}
