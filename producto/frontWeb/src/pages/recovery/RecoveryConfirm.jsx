import { useNavigate } from "react-router-dom";
import "../../styles/Recovery.css";
import loginImage from "../../assets/images/login-bg.jpg";

export default function RecoveryConfirm() {
  const navigate = useNavigate();

  return (
    <div className="recovery-container">
      <div className="recovery-panel">
        <div className="recovery-inner" style={{ textAlign: "center" }}>
          <h1 className="recovery-logo">
            Work<span>Sí</span>
          </h1>
          <h2 className="recovery-title">Confirma en tu bandeja</h2>
          <p className="recovery-text">
            Revisa tu correo para las instrucciones (mensaje simulado en MVP). Tu nueva contraseña
            ya quedó aplicada y puedes iniciar sesión.
          </p>
          <button
            type="button"
            className="recovery-btn"
            style={{ width: "100%" }}
            onClick={() => navigate("/")}
          >
            Volver a iniciar sesión
          </button>
        </div>
      </div>
      <div className="recovery-visual">
        <img src={loginImage} alt="" />
      </div>
    </div>
  );
}
