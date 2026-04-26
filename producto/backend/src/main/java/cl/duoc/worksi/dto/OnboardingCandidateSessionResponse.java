package cl.duoc.worksi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OnboardingCandidateSessionResponse {
    @JsonProperty("onboarding_id")
    private String onboardingId;

    @JsonProperty("onboarding_access_token")
    private String onboardingAccessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private long expiresIn;

    public OnboardingCandidateSessionResponse() {}

    public OnboardingCandidateSessionResponse(
        String onboardingId,
        String onboardingAccessToken,
        String tokenType,
        long expiresIn) {
        this.onboardingId = onboardingId;
        this.onboardingAccessToken = onboardingAccessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    public String getOnboardingId() {
        return onboardingId;
    }

    public void setOnboardingId(String onboardingId) {
        this.onboardingId = onboardingId;
    }

    public String getOnboardingAccessToken() {
        return onboardingAccessToken;
    }

    public void setOnboardingAccessToken(String onboardingAccessToken) {
        this.onboardingAccessToken = onboardingAccessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}