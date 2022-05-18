package com.unifin.jirareports.service;

import java.io.StringWriter;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service("emailService")
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(String toEmail, String body, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("jose.toledano_ext@unifin.com.mx");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);

        mailSender.send(message);
        System.out.println("Mail Send...");
    }

    public void sendEmailWithAttachment(String[] arrayToEmail, String body, String subject, StringWriter fw)
            throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, CharEncoding.UTF_8);

        mimeMessageHelper.setFrom("control.actividades@unifin.com.mx");
        mimeMessageHelper.setTo(arrayToEmail);
        mimeMessageHelper.setText(body);
        mimeMessageHelper.setSubject(subject);

        // attach the file into email body
        String fileName = subject+".csv";
		mimeMessageHelper.addAttachment(fileName,  new ByteArrayResource(fw.getBuffer().toString().getBytes()));

        mailSender.send(mimeMessage);
        System.out.println("Mail Send with attachment file...");

    }
}
