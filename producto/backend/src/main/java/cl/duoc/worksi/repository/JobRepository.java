package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.Job;
import cl.duoc.worksi.entity.enums.JobStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobRepository extends JpaRepository<Job, Long> {
  long countByStatus(JobStatus status);

  Page<Job> findByStatusAndPublishedByUserId(
      JobStatus status, Long publishedByUserId, Pageable pageable);

  @Query(
      "SELECT j FROM Job j WHERE j.publishedByUserId = :userId AND j.status IN :statuses"
          + " AND j.closingDate IS NOT NULL AND j.closingDate <= :closingOnOrBefore")
  Page<Job> findDueForClosingByPublishedByUserId(
      @Param("userId") Long publishedByUserId,
      @Param("statuses") Collection<JobStatus> statuses,
      @Param("closingOnOrBefore") LocalDate closingOnOrBefore,
      Pageable pageable);

  long countByStatusAndPublishedByUserId(JobStatus status, Long publishedByUserId);

  long countByStatusAndPublishedByUserIdAndPublishedAtGreaterThanEqualAndPublishedAtLessThan(
      JobStatus status,
      Long publishedByUserId,
      LocalDateTime publishedAtStartInclusive,
      LocalDateTime publishedAtEndExclusive);

  Page<Job> findByStatusOrderByCreatedAtDesc(JobStatus status, Pageable pageable);

  @Query(
      "SELECT j FROM Job j WHERE j.status = :st AND j.id NOT IN :excluded ORDER BY j.createdAt DESC")
  Page<Job> findByStatusAndIdNotIn(
      @Param("st") JobStatus status, @Param("excluded") Collection<Long> excluded, Pageable pageable);
}
