package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.Region;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
  List<Region> findByActiveIsTrueOrderByNameAsc();
}
