package cl.duoc.worksi.dto.company;

import cl.duoc.worksi.dto.MatchBreakdownResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class CompanyApplicationCandidatePreviewResponse {
  @JsonProperty("first_name")
  private final String firstName;

  @JsonProperty("middle_name")
  private final String middleName;

  @JsonProperty("last_name_paternal")
  private final String lastNamePaternal;

  @JsonProperty("last_name_maternal")
  private final String lastNameMaternal;

  @JsonProperty("sector_name")
  private final String sectorName;

  public CompanyApplicationCandidatePreviewResponse(
      String firstName,
      String middleName,
      String lastNamePaternal,
      String lastNameMaternal,
      String sectorName) {
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastNamePaternal = lastNamePaternal;
    this.lastNameMaternal = lastNameMaternal;
    this.sectorName = sectorName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public String getLastNamePaternal() {
    return lastNamePaternal;
  }

  public String getLastNameMaternal() {
    return lastNameMaternal;
  }

  public String getSectorName() {
    return sectorName;
  }
}
