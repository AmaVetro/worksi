package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {}
