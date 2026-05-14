package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.candidate.CandidateSwipeRequest;
import cl.duoc.worksi.entity.CandidateJobSwipe;
import cl.duoc.worksi.entity.Job;
import cl.duoc.worksi.entity.enums.JobStatus;
import cl.duoc.worksi.entity.enums.SwipeAction;
import cl.duoc.worksi.repository.CandidateJobSwipeRepository;
import cl.duoc.worksi.repository.JobRepository;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CandidateSwipeService {
  private final JobRepository jobRepository;
  private final CandidateJobSwipeRepository candidateJobSwipeRepository;

  public CandidateSwipeService(
      JobRepository jobRepository, CandidateJobSwipeRepository candidateJobSwipeRepository) {
    this.jobRepository = jobRepository;
    this.candidateJobSwipeRepository = candidateJobSwipeRepository;
  }

  @Transactional
  public void record(long candidateUserId, CandidateSwipeRequest request) {
    SwipeAction action = request.action();
    if (action != SwipeAction.PASS && action != SwipeAction.APPLY) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Acción inválida");
    }
    if (candidateJobSwipeRepository.existsByCandidateUserIdAndJobId(
        candidateUserId, request.jobId())) {
      return;
    }
    Optional<Job> opt = jobRepository.findById(request.jobId());
    if (opt.isEmpty() || opt.get().getStatus() != JobStatus.ACTIVE) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta no encontrada");
    }
    candidateJobSwipeRepository.save(
        CandidateJobSwipe.create(candidateUserId, request.jobId(), action));
  }
}
