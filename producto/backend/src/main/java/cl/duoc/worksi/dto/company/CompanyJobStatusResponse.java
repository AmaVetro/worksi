package cl.duoc.worksi.dto.company;

public class CompanyJobStatusResponse {
  private final long id;
  private final String status;

  public CompanyJobStatusResponse(long id, String status) {
    this.id = id;
    this.status = status;
  }

  public long getId() {
    return id;
  }

  public String getStatus() {
    return status;
  }
}
