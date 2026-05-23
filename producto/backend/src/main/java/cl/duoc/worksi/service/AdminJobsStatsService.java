package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.admin.AdminJobsStatsResponse;
import cl.duoc.worksi.entity.enums.JobStatus;
import cl.duoc.worksi.repository.JobRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminJobsStatsService {
  private final JobRepository jobRepository;

  public AdminJobsStatsService(JobRepository jobRepository) {
    this.jobRepository = jobRepository;
  }

  public AdminJobsStatsResponse activeJobsTotal() {
    long total = jobRepository.countByStatus(JobStatus.ACTIVE);
    return new AdminJobsStatsResponse(total);
  }
}
