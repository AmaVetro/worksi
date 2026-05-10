package cl.duoc.worksi.controller;

import cl.duoc.worksi.dto.CatalogListResponse;
import cl.duoc.worksi.service.CatalogService;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CatalogController {
  private final CatalogService catalogService;

  public CatalogController(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @GetMapping("/api/v1/catalogs/regions")
  public CatalogListResponse listRegions() {
    return catalogService.listRegions();
  }

  @GetMapping("/api/v1/catalogs/regions/{region_id}/communes")
  public CatalogListResponse listCommunes(@PathVariable("region_id") long regionId) {
    return catalogService.listCommunesByRegion(regionId);
  }

  @GetMapping("/api/v1/catalogs/sectors")
  public CatalogListResponse listSectors() {
    return catalogService.listSectors();
  }

  @GetMapping("/api/v1/catalogs/sectors/{sector_id}/skills")
  public ResponseEntity<CatalogListResponse> listSkills(@PathVariable("sector_id") long sectorId) {
    Optional<CatalogListResponse> body = catalogService.listSkillsBySector(sectorId);
    return body.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }
}
