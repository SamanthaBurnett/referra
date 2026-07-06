package com.sbproject.referra.controller;

import com.sbproject.referra.dto.CandidateProfileResponse;
import com.sbproject.referra.dto.CreateCandidateProfileRequest;
import com.sbproject.referra.dto.UpdateCandidateProfileRequest;
import com.sbproject.referra.service.CandidateProfileService;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class CandidateProfileController {

    private final CandidateProfileService candidateProfileService;

    public CandidateProfileController(CandidateProfileService candidateProfileService) {
        this.candidateProfileService = candidateProfileService;
    }

    @PostMapping("/api/candidate-profile")
    public CandidateProfileResponse createProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateCandidateProfileRequest request
    ) {
        return candidateProfileService.createProfile(jwt, request);
    }

    @GetMapping("/api/candidate-profile/me")
    public CandidateProfileResponse getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        return candidateProfileService.getProfile(jwt);
    }

    @PatchMapping("/api/candidate-profile/me")
    public CandidateProfileResponse updateMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateCandidateProfileRequest request
    ) {
        return candidateProfileService.updateProfile(jwt, request);
    }
}
