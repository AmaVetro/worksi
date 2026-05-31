import api from "./apiClient";

export async function getRecruiterCompanyProfile() {
  const { data } = await api.get("/api/v1/company/profile");
  return data;
}

export async function getRecruiterCompanyProfileImageBlob() {
  const { data } = await api.get("/api/v1/company/profile/image", {
    responseType: "blob",
  });
  return data;
}

export async function listMyJobs(page = 1, size = 20) {
  const { data } = await api.get("/api/v1/company/jobs", {
    params: { page, size, sort: "created_at,desc" },
  });
  return data;
}

export async function createJob(payload, imageFile) {
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
  const { data } = await api.post("/api/v1/company/jobs", form);
  return data;
}

export async function getJob(jobId) {
  const { data } = await api.get(`/api/v1/company/jobs/${jobId}`);
  return data;
}

export async function getJobImageBlob(jobId) {
  const { data } = await api.get(`/api/v1/company/jobs/${jobId}/image`, {
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
  const { data } = await api.patch(`/api/v1/company/jobs/${jobId}`, form);
  return data;
}
