package cl.duoc.worksi.entity;

import cl.duoc.worksi.entity.enums.Modality;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class CandidatePreferredModalityId implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private Long candidateUserId;
  private Modality modality;

  public CandidatePreferredModalityId() {}

  public Long getCandidateUserId() {
    return candidateUserId;
  }

  public Modality getModality() {
    return modality;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CandidatePreferredModalityId that = (CandidatePreferredModalityId) o;
    return Objects.equals(candidateUserId, that.candidateUserId) && modality == that.modality;
  }

  @Override
  public int hashCode() {
    return Objects.hash(candidateUserId, modality);
  }
}
