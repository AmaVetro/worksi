package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.OnboardingCandidateSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnboardingCandidateSessionRepository
    extends JpaRepository<OnboardingCandidateSession, String> {}