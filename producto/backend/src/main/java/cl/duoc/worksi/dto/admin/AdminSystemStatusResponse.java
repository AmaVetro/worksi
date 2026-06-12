package cl.duoc.worksi.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AdminSystemStatusResponse(
    @JsonProperty("backend") String backend,
    @JsonProperty("database") String database,
    @JsonProperty("ai") String ai) {}
