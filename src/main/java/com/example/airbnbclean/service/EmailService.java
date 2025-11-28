package com.example.airbnbclean.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

@Service
public class EmailService {

    private final String sendgridApiKey;

    public EmailService() {
        this.sendgridApiKey = System.getenv("SENDGRID_API_KEY");
        if (this.sendgridApiKey == null || this.sendgridApiKey.isEmpty()) {
            throw new IllegalStateException("SENDGRID_API_KEY is not set");
        }
    }

    public void sendEncryptedAttachment(String to, File zipFile) throws IOException {
        // FROM: you can keep your Gmail here so you receive it as the sender too
        Email from = new Email("walideamsaf15@gmail.com");
        Email toEmail = new Email(to);
        String subject = "New guest verification package";
        Content content = new Content("text/plain",
                "Attached is the encrypted passport/selfie ZIP (password: 1234).");

        Mail mail = new Mail(from, subject, toEmail, content);

        // Read and attach the ZIP file
        byte[] fileData = Files.readAllBytes(zipFile.toPath());
        String encoded = Base64.getEncoder().encodeToString(fileData);

        Attachments attachments = new Attachments();
        attachments.setType("application/zip");
        attachments.setFilename("verification.zip");
        attachments.setDisposition("attachment");
        attachments.setContent(encoded);
        mail.addAttachments(attachments);

        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);
        int status = response.getStatusCode();

        if (status >= 400) {
            throw new IOException("Failed to send email via SendGrid: " + status + " " + response.getBody());
        }
    }
}
