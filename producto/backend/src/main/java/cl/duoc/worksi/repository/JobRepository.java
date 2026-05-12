package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.Job;
import cl.duoc.worksi.entity.enums.JobStatus;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobRepository extends JpaRepository<Job, Long> {
  Page<Job> findByStatusAndPublishedByUserId(
      JobStatus status, Long publishedByUserId, Pageable pageable);

  Page<Job> findByStatusOrderByCreatedAtDesc(JobStatus status, Pageable pageable);

  @Query(
      "SELECT j FROM Job j WHERE j.status = :st AND j.id NOT IN :excluded ORDER BY j.createdAt DESC")
  Page<Job> findByStatusAndIdNotIn(
      @Param("st") JobStatus status, @Param("excluded") Collection<Long> excluded, Pageable pageable);
}
