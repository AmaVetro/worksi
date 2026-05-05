import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../services/authService";
import "../styles/Login.css";
import loginImage from "../assets/images/login-bg.jpg";

function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (!email || !password) {
        setError("Todos los campos son obligatorios");
        return;
        }

        try {
        setLoading(true);

        const data = await login(email, password);

        localStorage.setItem("token", data.access_token);
        localStorage.setItem("user", JSON.stringify(data.user));

        navigate("/home");

        } catch (err) {
        const message =
            err.response?.data?.error?.message ||
            "Error al iniciar sesión";

        setError(message);

        } finally {
        setLoading(false);
        }
    };

    return (
        <div className="login-container">
        
        {/* PANEL IZQUIERDO */}
        <div className="login-left">
            <div className="login-box">
            
            <h4 className="welcome-text">Bienvenido a</h4>
            
            <h1 className="logo">
                Work<span>Sy</span>
            </h1>

            <p className="subtitle">Empresas</p>

            <form onSubmit={handleSubmit} className="form">
                
                <input
                type="email"
                placeholder="Username"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="input"
                />

                <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="input"
                />

                <p className="forgot">Forgot Password?</p>

                {error && <p className="error">{error}</p>}

                <button type="submit" disabled={loading} className="button">
                {loading ? "Cargando..." : "Iniciar Sesión"}
                </button>

            </form>

            </div>
        </div>

        {/* PANEL DERECHO (IMAGEN) */}
        <div className="login-right">
            <img src={loginImage} alt="Login visual" />
        </div>

        </div>
    );
    }

    export default Login;