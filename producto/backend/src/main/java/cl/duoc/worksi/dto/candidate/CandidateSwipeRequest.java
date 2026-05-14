package cl.duoc.worksi.dto.candidate;

import cl.duoc.worksi.entity.enums.SwipeAction;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CandidateSwipeRequest(
    @JsonProperty("job_id") long jobId, SwipeAction action) {}
