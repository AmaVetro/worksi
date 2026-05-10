package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectorRepository extends JpaRepository<Sector, Long> {
  List<Sector> findByActiveIsTrueOrderByNameAsc();

  boolean existsByIdAndActiveIsTrue(Long id);
}
