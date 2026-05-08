import { useNavigate, useLocation } from "react-router-dom";
import "../../styles/Recovery.css";
import loginImage from "../../assets/images/login-bg.jpg";

export default function RecoveryLocked() {
  const navigate = useNavigate();
  const location = useLocation();
  const email = location.state?.email ?? "";
  const lockReason = location.state?.lockReason ?? "attempts_exceeded";

  const primaryMessage =
    lockReason === "already_locked"
      ? "Tu cuenta está bloqueada. Por nuestros lineamientos de seguridad debes recuperar tu contraseña para volver a iniciar sesión."
      : "Excediste el límite de intentos. Por nuestros lineamientos de seguridad, deberás recuperar tu contraseña.";

  return (
    <div className="recovery-container">
      <div className="recovery-panel">
        <div className="recovery-inner" style={{ textAlign: "center" }}>
          <h1 className="recovery-logo">
            Work<span>Sy</span>
          </h1>
          <p className="recovery-orange recovery-text">{primaryMessage}</p>
          <p className="recovery-text">Pulsa aquí:</p>
          <button
            type="button"
            className="recovery-btn"
            style={{ width: "100%" }}
            onClick={() => navigate("/recovery/forgot", { state: { email } })}
          >
            Recuperar contraseña
          </button>
          <button
            type="button"
            className="recovery-link-btn"
            onClick={() => navigate("/")}
          >
            Volver al inicio de sesión
          </button>
        </div>
      </div>
      <div className="recovery-visual">
        <img src={loginImage} alt="" />
      </div>
    </div>
  );
}
