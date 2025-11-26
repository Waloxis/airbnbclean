package com.example.airbnbclean.service;

import com.example.airbnbclean.exception.BadRequestException;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class VerificationService {

    private static final long MAX_SIZE = 8 * 1024 * 1024;
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png"};
    private static final char[] ZIP_PASSWORD = "1234".toCharArray();

    private final EmailService emailService;

    public VerificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public VerificationResult process(MultipartFile passport, MultipartFile selfie, boolean consent) {

        if (!consent) {
            throw new BadRequestException("Consent is required.");
        }

        validateFile(passport, "passport");
        validateFile(selfie, "selfie");

        Path dir = null;
        Path passportPath = null;
        Path selfiePath = null;
        File zipFile = null;

        try {
            // 1) Create temp dir and save files
            dir = Files.createTempDirectory("verification-");
            System.out.println("TEMP DIR = " + dir);

            passportPath = dir.resolve("passport-" + passport.getOriginalFilename());
            selfiePath   = dir.resolve("selfie-" + selfie.getOriginalFilename());

            Files.copy(passport.getInputStream(), passportPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(selfie.getInputStream(),   selfiePath,   StandardCopyOption.REPLACE_EXISTING);

            // 2) Create encrypted ZIP with password 1234
            Path zipPath = Files.createTempFile("passport-", ".zip");
            zipFile = zipPath.toFile();

            ZipFile zf = new ZipFile(zipFile, ZIP_PASSWORD);

            ZipParameters params = new ZipParameters();
            params.setEncryptFiles(true);
            params.setEncryptionMethod(EncryptionMethod.AES);

            // null safety + existence check
            if (passportPath != null && Files.exists(passportPath)) {
                zf.addFile(passportPath.toFile(), params);
            }
            if (selfiePath != null && Files.exists(selfiePath)) {
                zf.addFile(selfiePath.toFile(), params);
            }

            // 3) Email the encrypted ZIP
            emailService.sendEncryptedAttachment("walideamsaf15@gmail.com", zipFile);

            // 4) Mock verification result
            double mockScore = 0.87;
            String mockStatus = mockScore >= 0.8 ? "PASSED" : "FAILED";
            return new VerificationResult(mockStatus, mockScore);

        } catch (IOException e) {
            e.printStackTrace();
            throw new BadRequestException("Failed to store or encrypt files.");

        }
    }

    private void validateFile(MultipartFile file, String label) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(label + " file is missing.");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new BadRequestException(label + " file is too large (max 8MB).");
        }

        String type = file.getContentType();
        boolean ok = false;
        for (String allowed : ALLOWED_TYPES) {
            if (allowed.equals(type)) ok = true;
        }
        if (!ok) {
            throw new BadRequestException(label + " must be JPG or PNG.");
        }
    }


}
