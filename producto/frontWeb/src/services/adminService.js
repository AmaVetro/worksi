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

export async function getCompany(companyId) {
  const { data } = await api.get(`/api/v1/admin/companies/${companyId}`);
  return data;
}

export async function updateCompany(companyId, payload, imageFile) {
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
  const { data } = await api.patch(`/api/v1/admin/companies/${companyId}`, form);
  return data;
}

export async function deleteCompany(companyId) {
  const { data } = await api.delete(`/api/v1/admin/companies/${companyId}`);
  return data;
}

export async function deleteRecruiter(recruiterUserId) {
  const { data } = await api.delete(
    `/api/v1/admin/recruiters/${recruiterUserId}`
  );
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
        phone: c.phone ?? "",
        region_name: c.region_name ?? c.regionName ?? "",
        commune_name: c.commune_name ?? c.communeName ?? "",
        sector_name: c.sector_name ?? c.sectorName ?? "",
      };
    })
    .filter(Boolean);
  return { ...(data && typeof data === "object" ? data : {}), items };
}

export async function createRecruiter(payload) {
  const { data } = await api.post("/api/v1/admin/recruiters", payload);
  return data;
}

export async function getRecruiter(recruiterUserId) {
  const { data } = await api.get(`/api/v1/admin/recruiters/${recruiterUserId}`);
  return data;
}

export async function updateRecruiter(recruiterUserId, payload) {
  const { data } = await api.patch(
    `/api/v1/admin/recruiters/${recruiterUserId}`,
    payload
  );
  return data;
}

export async function listRecruiters(page = 1, size = 50) {
  const { data } = await api.get("/api/v1/admin/recruiters", {
    params: { page, size, sort: "created_at,desc" },
  });
  return data;
}

export async function getActiveJobsTotal() {
  const { data } = await api.get("/api/v1/admin/jobs/stats");
  const raw = data?.active_jobs_total ?? data?.activeJobsTotal;
  const n = Number(raw);
  return Number.isFinite(n) ? n : 0;
}

export async function getSystemStatus() {
  const { data } = await api.get("/api/v1/admin/system/status");
  return {
    backend: data?.backend ?? "DOWN",
    database: data?.database ?? "DOWN",
    ai: data?.ai ?? "DOWN",
  };
}

export async function listJobs(
  page = 1,
  size = 50,
  status = "ACTIVE",
  companyName = "",
  title = ""
) {
  const { data } = await api.get("/api/v1/admin/jobs", {
    params: {
      page,
      size,
      sort: "created_at,desc",
      status,
      company_name: companyName,
      title,
    },
  });
  return data;
}

export async function patchJobStatus(jobId, status) {
  const { data } = await api.patch(`/api/v1/admin/jobs/${jobId}/status`, {
    status,
  });
  return data;
}

export async function deleteJob(jobId) {
  const { data } = await api.delete(`/api/v1/admin/jobs/${jobId}`);
  return data;
}

export async function getJob(jobId) {
  const { data } = await api.get(`/api/v1/admin/jobs/${jobId}`);
  return data;
}

export async function getJobImageBlob(jobId) {
  const { data } = await api.get(`/api/v1/admin/jobs/${jobId}/image`, {
    responseType: "blob",
  });
  return data;
}

export async function updateJob(jobId, payload, imageFile) {
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
  const { data } = await api.patch(`/api/v1/admin/jobs/${jobId}`, form);
  return data;
}
