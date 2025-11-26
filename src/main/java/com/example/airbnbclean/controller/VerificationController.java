package com.example.airbnbclean.controller;


import com.example.airbnbclean.service.VerificationResult;
import com.example.airbnbclean.verification.VerificationResponse;
import com.example.airbnbclean.service.VerificationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class VerificationController {

    private final VerificationService service;

    public VerificationController(VerificationService service) {
        this.service = service;
    }

    @PostMapping("/verification")
    public VerificationResponse submit(
            @RequestPart("passport") MultipartFile passport,
            @RequestPart("selfie") MultipartFile selfie,
            @RequestParam("consent") boolean consent
    ) {
        VerificationResult result = service.process(passport, selfie, consent);

        return new VerificationResponse(
                true,
                result.getStatus(),
                result.getScore(),
                "Verification finished (mock)."
        );
    }

}
