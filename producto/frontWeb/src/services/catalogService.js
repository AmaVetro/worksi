import api from "./apiClient";

export async function fetchRegions() {
  const { data } = await api.get("/api/v1/catalogs/regions");
  return data.items || [];
}

export async function fetchCommunes(regionId) {
  const { data } = await api.get(`/api/v1/catalogs/regions/${regionId}/communes`);
  return data.items || [];
}

export async function fetchSectors() {
  const { data } = await api.get("/api/v1/catalogs/sectors");
  return data.items || [];
}

export async function fetchSkillsBySector(sectorId) {
  const { data } = await api.get(`/api/v1/catalogs/sectors/${sectorId}/skills`);
  return data.items || [];
}
