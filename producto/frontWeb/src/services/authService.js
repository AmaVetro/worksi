import axios from "axios";

const API_BASE = "";

export function decodeJwtPayload(token) {
  try {
    const parts = String(token).split(".");
    if (parts.length < 2) return null;
    let base64 = parts[1].replace(/-/g, "+").replace(/_/g, "/");
    const pad = base64.length % 4;
    if (pad) base64 += "=".repeat(4 - pad);
    return JSON.parse(atob(base64));
  } catch {
    return null;
  }
}

export function normalizeRole(raw) {
  const s = (raw == null ? "" : String(raw)).trim().toUpperCase();
  if (["ADMIN", "RECRUITER", "CANDIDATE"].includes(s)) return s;
  return "";
}

export function deriveSessionFromLoginBody(data) {
  const tokenRaw = data?.access_token ?? data?.accessToken;
  const token = typeof tokenRaw === "string" ? tokenRaw.trim() : "";
  const payload = token ? decodeJwtPayload(token) : null;
  let user = data?.user;
  if (!user || typeof user !== "object") {
    if (payload && payload.sub) {
      user = {
        id: Number(payload.sub),
        email: typeof payload.email === "string" ? payload.email : "",
        role: normalizeRole(payload.role),
      };
    } else {
      user = null;
    }
  } else {
    const rFromUser = normalizeRole(user.role);
    const rFromJwt = payload ? normalizeRole(payload.role) : "";
    user = {
      ...user,
      role: rFromUser || rFromJwt,
    };
  }
  return { token, user };
}

export const login = async (email, password) => {
  const response = await axios.post(`${API_BASE}/api/v1/auth/login`, {
    email,
    password,
  });
  return response.data;
};

export function parseLoginBlockedDerivation(err) {
    const status = err.response?.status;
    const errorPayload = err.response?.data?.error;
    if (
        status !== 422 ||
        !errorPayload ||
        errorPayload.code !== "BUSINESS_RULE_VIOLATION"
    ) {
        return null;
    }
    const msg = String(errorPayload.message || "");
    if (msg.includes("intentos fallidos")) {
        return { lockReason: "attempts_exceeded" };
    }
    return { lockReason: "already_locked" };
}