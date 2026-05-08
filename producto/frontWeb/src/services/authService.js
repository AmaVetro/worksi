import axios from "axios";

const API_BASE = "";

export const login = async (email, password) => {
    const response = await axios.post(`${API_BASE}/api/v1/auth/login`, {
        email,
        password
    });
    return response.data;
};