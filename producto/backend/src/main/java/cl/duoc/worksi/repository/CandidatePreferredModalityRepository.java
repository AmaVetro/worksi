package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.CandidatePreferredModality;
import cl.duoc.worksi.entity.CandidatePreferredModalityId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidatePreferredModalityRepository
    extends JpaRepository<CandidatePreferredModality, CandidatePreferredModalityId> {}
