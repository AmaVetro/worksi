package cl.duoc.worksi.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AdminJobsStatsResponse(
    @JsonProperty("active_jobs_total") long activeJobsTotal) {}
