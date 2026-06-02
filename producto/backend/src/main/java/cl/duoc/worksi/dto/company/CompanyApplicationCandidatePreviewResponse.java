package cl.duoc.worksi.dto.company;

import cl.duoc.worksi.dto.MatchBreakdownResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class CompanyApplicationCandidatePreviewResponse {
  @JsonProperty("first_name")
  private final String firstName;

  @JsonProperty("last_name_paternal")
  private final String lastNamePaternal;

  @JsonProperty("sector_name")
  private final String sectorName;

  public CompanyApplicationCandidatePreviewResponse(
      String firstName, String lastNamePaternal, String sectorName) {
    this.firstName = firstName;
    this.lastNamePaternal = lastNamePaternal;
    this.sectorName = sectorName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastNamePaternal() {
    return lastNamePaternal;
  }

  public String getSectorName() {
    return sectorName;
  }
}
