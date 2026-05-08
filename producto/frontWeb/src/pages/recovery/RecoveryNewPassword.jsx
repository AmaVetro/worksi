import { useState } from "react";
import { useNavigate, useLocation, Navigate } from "react-router-dom";
import { resetPasswordWithRecovery } from "../../services/passwordRecoveryService";
import { passwordMatches } from "../../utils/passwordPolicy";
import "../../styles/Recovery.css";
import loginImage from "../../assets/images/login-bg.jpg";

export default function RecoveryNewPassword() {
  const navigate = useNavigate();
  const location = useLocation();
  const email = location.state?.email;
  const recovery_token = location.state?.recovery_token;

  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  if (!email || !recovery_token) {
    return <Navigate to="/recovery/forgot" replace />;
  }

  const valid = passwordMatches(password);

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    if (!valid) {
      setError("La contraseña no cumple la política del sistema.");
      return;
    }
    try {
      setLoading(true);
      await resetPasswordWithRecovery(email, recovery_token, password);
      navigate("/recovery/confirm");
    } catch (err) {
      const msg =
        err.response?.data?.error?.message ||
        "No fue posible actualizar la contraseña.";
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
          onClick={() =>
            navigate("/recovery/code", {
              state: { email },
            })
          }
          aria-label="Volver"
        >
          ←
        </button>
        <div className="recovery-inner">
          <h1 className="recovery-logo">
            Work<span>Sy</span>
          </h1>
          <h2 className="recovery-title">Nueva contraseña</h2>
          <form className="recovery-form" onSubmit={submit}>
            <input
              className="recovery-input"
              type="password"
              autoComplete="new-password"
              placeholder="Nueva contraseña"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            {error ? <p className="recovery-error">{error}</p> : null}
            <p className="recovery-hint">Mínimo 10 caracteres.</p>
            <p className="recovery-hint">
              Al menos una mayúscula, una minúscula, un número y un símbolo.
            </p>
            <button
              type="submit"
              className="recovery-btn"
              disabled={loading || !valid}
            >
              {loading ? "Guardando..." : "Aceptar"}
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
