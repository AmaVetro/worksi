import api from "./apiClient";

export async function createCompany(payload, imageFile) {
  const json = JSON.stringify(payload);
  const form = new FormData();
  form.append(
    "data",
    new Blob([json], { type: "application/json" }),
    "data.json"
  );
  if (imageFile) {
    form.append("image", imageFile);
  }
  const { data } = await api.post("/api/v1/admin/companies", form);
  return data;
}

export async function listCompanies(page = 1, size = 50) {
  const { data } = await api.get("/api/v1/admin/companies", {
    params: { page, size, sort: "created_at,desc" },
  });
  const raw = Array.isArray(data)
    ? data
    : (data?.items ?? data?.content ?? []);
  const list = Array.isArray(raw) ? raw : [];
  const items = list
    .map((c) => {
      if (!c || typeof c !== "object") return null;
      const id = c.company_id ?? c.companyId ?? c.id;
      const name = c.commercial_name ?? c.commercialName ?? "";
      const legal = c.legal_name ?? c.legalName ?? "";
      const rut = c.rut ?? "";
      const corp = c.corporate_email ?? c.corporateEmail ?? "";
      if (id == null || id === "") return null;
      const n = Number(id);
      if (Number.isNaN(n)) return null;
      return {
        company_id: n,
        commercial_name: String(name),
        legal_name: legal,
        rut,
        corporate_email: String(corp),
      };
    })
    .filter(Boolean);
  return { ...(data && typeof data === "object" ? data : {}), items };
}

export async function createRecruiter(payload) {
  const { data } = await api.post("/api/v1/admin/recruiters", payload);
  return data;
}

export async function getActiveJobsTotal() {
  const { data } = await api.get("/api/v1/admin/jobs/stats");
  const raw = data?.active_jobs_total ?? data?.activeJobsTotal;
  const n = Number(raw);
  return Number.isFinite(n) ? n : 0;
}
