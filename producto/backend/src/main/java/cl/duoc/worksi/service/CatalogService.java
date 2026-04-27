package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.CatalogItemResponse;
import cl.duoc.worksi.dto.CatalogListResponse;
import cl.duoc.worksi.repository.CommuneRepository;
import cl.duoc.worksi.repository.RegionRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
  private final RegionRepository regionRepository;
  private final CommuneRepository communeRepository;

  public CatalogService(RegionRepository regionRepository, CommuneRepository communeRepository) {
    this.regionRepository = regionRepository;
    this.communeRepository = communeRepository;
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
}
