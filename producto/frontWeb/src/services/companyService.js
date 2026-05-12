import api from "./apiClient";

export async function listMyJobs(page = 1, size = 20) {
  const { data } = await api.get("/api/v1/company/jobs", {
    params: { page, size, sort: "created_at,desc" },
  });
  return data;
}

export async function createJob(payload) {
  const { data } = await api.post("/api/v1/company/jobs", payload);
  return data;
}

export async function getJob(jobId) {
  const { data } = await api.get(`/api/v1/company/jobs/${jobId}`);
  return data;
}
