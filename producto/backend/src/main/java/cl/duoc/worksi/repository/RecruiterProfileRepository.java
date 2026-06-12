package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Long> {
  boolean existsByRut(String rut);

  boolean existsByRutAndUserIdNot(String rut, Long userId);

  long countByCompanyId(Long companyId);
}
