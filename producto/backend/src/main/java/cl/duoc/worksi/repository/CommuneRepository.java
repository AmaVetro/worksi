package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.Commune;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommuneRepository extends JpaRepository<Commune, Long> {
  @Query("SELECT COUNT(c) > 0 FROM Commune c WHERE c.id = ?1 AND c.regionId = ?2")
  boolean existsByIdAndRegionId(Long communeId, Long regionId);

  List<Commune> findByRegionIdAndActiveIsTrueOrderByNameAsc(Long regionId);
}
