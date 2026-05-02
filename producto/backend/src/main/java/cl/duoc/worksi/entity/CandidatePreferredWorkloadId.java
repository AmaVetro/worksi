package cl.duoc.worksi.entity;

import cl.duoc.worksi.entity.enums.Workload;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class CandidatePreferredWorkloadId implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private Long candidateUserId;
  private Workload workload;

  public CandidatePreferredWorkloadId() {}

  public Long getCandidateUserId() {
    return candidateUserId;
  }

  public Workload getWorkload() {
    return workload;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CandidatePreferredWorkloadId that = (CandidatePreferredWorkloadId) o;
    return Objects.equals(candidateUserId, that.candidateUserId) && workload == that.workload;
  }

  @Override
  public int hashCode() {
    return Objects.hash(candidateUserId, workload);
  }
}
