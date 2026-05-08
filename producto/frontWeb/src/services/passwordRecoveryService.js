import axios from "axios";

const API_BASE = "";

export async function requestRecoveryCode(email) {
  const response = await axios.post(
    `${API_BASE}/api/v1/auth/password-recovery/request`,
    { email }
  );
  return response.data;
}

export async function verifyRecoveryCode(email, code) {
  const response = await axios.post(
    `${API_BASE}/api/v1/auth/password-recovery/verify`,
    { email, code }
  );
  return response.data;
}

export async function resetPasswordWithRecovery(email, recovery_token, new_password) {
  const response = await axios.post(
    `${API_BASE}/api/v1/auth/password-recovery/reset`,
    { email, recovery_token, new_password }
  );
  return response.data;
}
