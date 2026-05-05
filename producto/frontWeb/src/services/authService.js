import axios from "axios";

const API = "http://localhost:8080"; // ajusta si cambia

export const login = async (email, password) => {
    const response = await axios.post(`${API}/auth/login`, {
        email,
        password
    });
    return response.data;
};