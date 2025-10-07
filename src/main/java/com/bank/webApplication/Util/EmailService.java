package com.bank.webApplication.Util;

import com.bank.webApplication.Dto.MailBodyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    public void sendMessage(MailBodyDTO mailBodyDTO){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailBodyDTO.to());
        message.setFrom("monicaglory05@gmail.com");
        message.setSubject(mailBodyDTO.subject());
        message.setText(mailBodyDTO.text());

        javaMailSender.send(message);
    }
}

