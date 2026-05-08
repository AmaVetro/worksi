import { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { requestRecoveryCode } from "../../services/passwordRecoveryService";
import "../../styles/Recovery.css";
import loginImage from "../../assets/images/login-bg.jpg";

export default function RecoveryForgot() {
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState(location.state?.email ?? "");

  useEffect(() => {
    const next = location.state?.email;
    if (typeof next === "string" && next.length > 0) {
      setEmail(next);
    }
  }, [location.state?.email]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    const trimmed = email.trim();
    if (!trimmed) {
      setError("Completa este campo");
      return;
    }
    try {
      setLoading(true);
      await requestRecoveryCode(trimmed);
      navigate("/recovery/code", { state: { email: trimmed } });
    } catch (err) {
      const msg =
        err.response?.data?.error?.message ||
        "No fue posible continuar. Verifica el correo.";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="recovery-container">
      <div className="recovery-panel">
        <button
          type="button"
          className="recovery-back"
          onClick={() => navigate("/")}
          aria-label="Volver"
        >
          ←
        </button>
        <div className="recovery-inner">
          <h1 className="recovery-logo">
            Work<span>Sy</span>
          </h1>
          <h2 className="recovery-title">¿Olvidaste tu contraseña?</h2>
          <p className="recovery-text">
            Ingresa el correo asociado a tu cuenta y pulsa Enviar código para continuar con el
            flujo de recuperación (MVP demostración).
          </p>
          <form className="recovery-form" onSubmit={submit}>
            <input
              className="recovery-input"
              type="email"
              autoComplete="email"
              placeholder="Correo electrónico"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            {error ? <p className="recovery-error">{error}</p> : null}
            <button type="submit" className="recovery-btn" disabled={loading}>
              {loading ? "Enviando..." : "Enviar código"}
            </button>
          </form>
        </div>
      </div>
      <div className="recovery-visual">
        <img src={loginImage} alt="" />
      </div>
    </div>
  );
}
