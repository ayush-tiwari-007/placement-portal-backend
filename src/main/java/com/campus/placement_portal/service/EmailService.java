
package com.campus.placement_portal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        // OTP user ke email par jayega
        message.setTo(toEmail);

        // Brevo me verified sender
        message.setFrom("ayushtiwari0015@gmail.com");

        message.setSubject(
                "Campus Placement Portal - Password Reset OTP"
        );

        message.setText(
                "Hello,\n\n"
                + "Your OTP for resetting your Campus Placement Portal password is:\n\n"
                + otp
                + "\n\n"
                + "This OTP is valid for 5 minutes.\n\n"
                + "If you did not request a password reset, please ignore this email.\n\n"
                + "Regards,\n"
                + "Campus Placement Portal"
        );

        mailSender.send(message);
    }
}

