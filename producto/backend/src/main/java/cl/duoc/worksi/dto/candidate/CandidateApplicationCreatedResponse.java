package cl.duoc.worksi.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record CandidateApplicationCreatedResponse(
    @JsonProperty("application_id") long applicationId,
    String status,
    @JsonProperty("applied_at") Instant appliedAt) {}
