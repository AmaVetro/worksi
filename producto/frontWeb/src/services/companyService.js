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

export async function listMyJobs(page = 1, size = 20, status = "ACTIVE") {
  const { data } = await api.get("/api/v1/company/jobs", {
    params: { page, size, sort: "created_at,desc", status },
  });
  return data;
}

export async function patchJobStatus(jobId, status) {
  const { data } = await api.patch(`/api/v1/company/jobs/${jobId}/status`, {
    status,
  });
  return data;
}

export async function deleteJob(jobId) {
  const { data } = await api.delete(`/api/v1/company/jobs/${jobId}`);
  return data;
}

export async function getMyJobStats() {
  const { data } = await api.get("/api/v1/company/jobs/stats");
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

export async function listJobApplications(jobId, page = 1, size = 20) {
  const { data } = await api.get(`/api/v1/company/jobs/${jobId}/applications`, {
    params: { page, size, sort: "match_score,desc" },
  });
  return data;
}

export async function getJobApplication(jobId, applicationId) {
  const { data } = await api.get(
    `/api/v1/company/jobs/${jobId}/applications/${applicationId}`
  );
  return data;
}

export async function getCandidateProfileForApplication(jobId, applicationId) {
  const { data } = await api.get(
    `/api/v1/company/jobs/${jobId}/applications/${applicationId}/candidate-profile`
  );
  return data;
}

export async function getApplicationCvFile(jobId, applicationId, download = false) {
  const response = await api.get(
    `/api/v1/company/jobs/${jobId}/applications/${applicationId}/cv/file`,
    {
      params: download ? { download: true } : {},
      responseType: "blob",
    }
  );
  return response;
}
