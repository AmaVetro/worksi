package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.CandidatePreferredWorkload;
import cl.duoc.worksi.entity.CandidatePreferredWorkloadId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidatePreferredWorkloadRepository
    extends JpaRepository<CandidatePreferredWorkload, CandidatePreferredWorkloadId> {}
