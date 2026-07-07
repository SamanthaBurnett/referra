package com.sbproject.referra.controller;

import com.sbproject.referra.dto.CreateReferrerProfileRequest;
import com.sbproject.referra.dto.ReferrerProfileResponse;
import com.sbproject.referra.dto.UpdateReferrerProfileRequest;
import com.sbproject.referra.service.ReferrerProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReferrerProfileController {

    private final ReferrerProfileService referrerProfileService;

    public ReferrerProfileController(ReferrerProfileService referrerProfileService) {
        this.referrerProfileService = referrerProfileService;
    }

    @PostMapping("/api/referrer-profile")
    public ReferrerProfileResponse createProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateReferrerProfileRequest request
    ) {
        return referrerProfileService.createProfile(jwt, request);
    }

    @GetMapping("/api/referrer-profile/me")
    public ReferrerProfileResponse getProfile(@AuthenticationPrincipal Jwt jwt) {
        return referrerProfileService.getProfile(jwt);
    }

    @PatchMapping("/api/referrer-profile/me")
    public ReferrerProfileResponse updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateReferrerProfileRequest request
    ) {
        return referrerProfileService.updateProfile(jwt, request);
    }
}
