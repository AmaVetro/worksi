package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {}
