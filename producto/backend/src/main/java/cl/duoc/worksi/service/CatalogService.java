package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.CatalogItemResponse;
import cl.duoc.worksi.dto.CatalogListResponse;
import cl.duoc.worksi.entity.Sector;
import cl.duoc.worksi.repository.CommuneRepository;
import cl.duoc.worksi.repository.RegionRepository;
import cl.duoc.worksi.repository.SectorRepository;
import cl.duoc.worksi.repository.SkillRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
  private final RegionRepository regionRepository;
  private final CommuneRepository communeRepository;
  private final SectorRepository sectorRepository;
  private final SkillRepository skillRepository;

  public CatalogService(
      RegionRepository regionRepository,
      CommuneRepository communeRepository,
      SectorRepository sectorRepository,
      SkillRepository skillRepository) {
    this.regionRepository = regionRepository;
    this.communeRepository = communeRepository;
    this.sectorRepository = sectorRepository;
    this.skillRepository = skillRepository;
  }

  public CatalogListResponse listRegions() {
    List<CatalogItemResponse> items =
        regionRepository.findByActiveIsTrueOrderByNameAsc().stream()
            .map(r -> new CatalogItemResponse(r.getId(), r.getCode(), r.getName()))
            .toList();
    return new CatalogListResponse(items);
  }

  public CatalogListResponse listCommunesByRegion(long regionId) {
    List<CatalogItemResponse> items =
        communeRepository.findByRegionIdAndActiveIsTrueOrderByNameAsc(regionId).stream()
            .map(c -> new CatalogItemResponse(c.getId(), c.getCode(), c.getName()))
            .toList();
    return new CatalogListResponse(items);
  }

  public CatalogListResponse listSectors() {
    List<CatalogItemResponse> items =
        sectorRepository.findByActiveIsTrueOrderByNameAsc().stream()
            .map(s -> new CatalogItemResponse(s.getId(), s.getCode(), s.getName()))
            .toList();
    return new CatalogListResponse(items);
  }

  public Optional<CatalogListResponse> listSkillsBySector(long sectorId) {
    Optional<Sector> sector = sectorRepository.findById(sectorId);
    if (sector.isEmpty() || !sector.get().isActive()) {
      return Optional.empty();
    }
    List<CatalogItemResponse> items =
        skillRepository.findActiveBySectorId(sectorId).stream()
            .map(sk -> new CatalogItemResponse(sk.getId(), sk.getCode(), sk.getName()))
            .toList();
    return Optional.of(new CatalogListResponse(items));
  }
}
