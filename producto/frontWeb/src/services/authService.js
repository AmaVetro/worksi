import axios from "axios";

const API_BASE = "";

export const login = async (email, password) => {
    const response = await axios.post(`${API_BASE}/api/v1/auth/login`, {
        email,
        password
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