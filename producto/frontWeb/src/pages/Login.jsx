import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login, parseLoginBlockedDerivation, deriveSessionFromLoginBody } from "../services/authService";
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
          const { token, user } = deriveSessionFromLoginBody(data);
          if (!token) {
            setError("Respuesta de login invalida");
            return;
          }
          if (!user || !user.role) {
            setError("Respuesta de login invalida");
            return;
          }
          localStorage.setItem("token", token);
          localStorage.setItem("user", JSON.stringify(user));
          const r = user.role;
          if (r === "ADMIN") {
            navigate("/home");
          } else if (r === "RECRUITER") {
            navigate("/recruiter/home");
          } else {
            navigate("/home");
          }
        } catch (err) {
          const blocked = parseLoginBlockedDerivation(err);
          if (blocked) {
            navigate("/recovery/locked", {
              state: {
                email: email.trim(),
                lockReason: blocked.lockReason,
              },
            });
            return;
          }
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

        <div className="login-left">
            <div className="login-box">

            <h4 className="welcome-text">Bienvenido a</h4>

            <h1 className="logo">
                Work<span>Sí</span>
            </h1>

            <p className="subtitle">Empresas</p>

            <form onSubmit={handleSubmit} className="form">

                <input
                type="email"
                placeholder="Correo electrónico"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="input"
                />

                <input
                type="password"
                placeholder="Contraseña"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="input"
                />

                <button
                type="button"
                className="forgot"
                onClick={() => navigate("/recovery/forgot")}
                >
                ¿Olvidaste tu contraseña?
                </button>

                {error && <p className="error">{error}</p>}

                <button type="submit" disabled={loading} className="button">
                {loading ? "Cargando..." : "Iniciar sesión"}
                </button>

            </form>

            </div>
        </div>

        <div className="login-right">
            <img src={loginImage} alt="Login visual" />
        </div>

        </div>
    );
    }

    export default Login;