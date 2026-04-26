package cl.duoc.worksi.controller;

import cl.duoc.worksi.dto.OnboardingCandidateSessionRequest;
import cl.duoc.worksi.dto.OnboardingCandidateSessionResponse;
import cl.duoc.worksi.service.OnboardingCandidateSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OnboardingCandidateController {
    private final OnboardingCandidateSessionService onboardingCandidateSessionService;

    public OnboardingCandidateController(
        OnboardingCandidateSessionService onboardingCandidateSessionService) {
        this.onboardingCandidateSessionService = onboardingCandidateSessionService;
    }

    @PostMapping("/api/v1/onboarding/candidate/session")
    @ResponseStatus(HttpStatus.CREATED)
    public OnboardingCandidateSessionResponse openCandidateOnboardingSession(
        @RequestBody OnboardingCandidateSessionRequest request) {
        return onboardingCandidateSessionService.openSession(request);
    }
}