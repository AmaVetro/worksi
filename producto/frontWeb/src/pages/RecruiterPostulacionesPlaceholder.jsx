import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import "../styles/Home.css";

export default function RecruiterPostulacionesPlaceholder() {
  const navigate = useNavigate();
  return (
    <div>
      <Navbar />
      <div className="home-container">
        <div className="home-content">
          <div className="recruitment-card" style={{ maxWidth: 640 }}>
            <h3>Postulaciones de postulantes</h3>
            <p style={{ color: "#555", lineHeight: 1.5 }}>
              Aquí se listarán las postulaciones por oferta según el flujo oficial
              cuando el endpoint de empresa esté disponible en el backend.
            </p>
            <button
              type="button"
              className="secondary-btn"
              style={{ marginTop: 16 }}
              onClick={() => navigate("/recruiter/home")}
            >
              Volver al inicio
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
