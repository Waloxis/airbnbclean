package com.example.airbnbclean.service;



import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEncryptedAttachment(String to, File zipFile) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Encrypted passport file");
            helper.setText("""
                    The passport file is attached as an encrypted ZIP.
                    Password: 1234
                    """);

            helper.addAttachment(zipFile.getName(), zipFile);

            mailSender.send(message);
        } catch (MessagingException e) {
            // In real life you'd log this properly
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
