package cl.duoc.worksi.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CandidateApplicationRequest(@JsonProperty("job_id") long jobId) {}
