import { useState } from "react";
import { useNavigate, useLocation, Navigate } from "react-router-dom";
import {
  requestRecoveryCode,
  verifyRecoveryCode,
} from "../../services/passwordRecoveryService";
import "../../styles/Recovery.css";
import loginImage from "../../assets/images/login-bg.jpg";

export default function RecoveryCode() {
  const navigate = useNavigate();
  const location = useLocation();
  const email = location.state?.email;

  const [code, setCode] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  if (!email) {
    return <Navigate to="/recovery/forgot" replace />;
  }

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    const c = code.trim();
    if (!c) {
      setError("Completa este campo");
      return;
    }
    try {
      setLoading(true);
      const data = await verifyRecoveryCode(email, c);
      navigate("/recovery/new-password", {
        state: { email, recovery_token: data.recovery_token },
      });
    } catch (err) {
      const msg =
        err.response?.data?.error?.message ||
        "No fue posible validar. Solicita un nuevo código e intenta de nuevo.";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  const resend = async () => {
    setError("");
    try {
      setLoading(true);
      await requestRecoveryCode(email);
    } catch (err) {
      const msg =
        err.response?.data?.error?.message || "No fue posible reenviar.";
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
          onClick={() => navigate("/recovery/forgot", { state: { email } })}
          aria-label="Volver"
        >
          ←
        </button>
        <div className="recovery-inner">
          <h1 className="recovery-logo">
            Work<span>Sí</span>
          </h1>
          <h2 className="recovery-title">Ingresa tu código</h2>
          <p className="recovery-hint">
            En MVP puedes escribir cualquier texto en el campo y pulsar Ingresar código para
            continuar.
          </p>
          <form className="recovery-form" onSubmit={submit}>
            <input
              className="recovery-input"
              type="text"
              autoComplete="one-time-code"
              placeholder="Código"
              value={code}
              onChange={(e) => setCode(e.target.value)}
            />
            {error ? <p className="recovery-error">{error}</p> : null}
            <button type="submit" className="recovery-btn" disabled={loading}>
              {loading ? "Comprobando..." : "Ingresar código"}
            </button>
          </form>
          <p className="recovery-text" style={{ textAlign: "center", marginTop: "8px" }}>
            ¿No recibiste el código?
          </p>
          <button type="button" className="recovery-btn" disabled={loading} onClick={resend}>
            Enviar nuevo código
          </button>
        </div>
      </div>
      <div className="recovery-visual">
        <img src={loginImage} alt="" />
      </div>
    </div>
  );
}
