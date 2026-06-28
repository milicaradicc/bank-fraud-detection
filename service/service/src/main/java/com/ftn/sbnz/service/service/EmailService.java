package com.ftn.sbnz.service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationCode(String toEmail, String code, double amount, String txId) {
        if (fromAddress == null || fromAddress.isBlank()) {
            System.out.println("[STEP-UP] Email nije konfigurisan. Kod za " + txId + ": " + code);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("FraudGuard — Potvrda transakcije " + txId);
        message.setText(
                "Detektovana je sumnjiva aktivnost na vašem nalogu.\n\n" +
                        "Transakcija: " + txId + "\n" +
                        "Iznos: " + amount + " EUR\n\n" +
                        "Vaš kod za potvrdu je: " + code + "\n\n" +
                        "Ako niste vi inicirali ovu transakciju, NE unosite kod i odmah kontaktirajte banku."
        );
        mailSender.send(message);
    }
}
