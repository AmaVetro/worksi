import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import "../styles/Home.css";

export default function RecruiterReclutamiento() {
  const navigate = useNavigate();
  return (
    <div>
      <Navbar />
      <div className="home-container">
        <div className="home-content">
          <div className="actions-container" style={{ flexWrap: "wrap" }}>
            <div className="action-card" style={{ minWidth: 200 }}>
              <p>Ofertas publicadas</p>
              <button
                type="button"
                className="secondary-btn"
                onClick={() => navigate("/recruiter/ofertas")}
              >
                Ir
              </button>
            </div>
            <div className="action-card" style={{ minWidth: 200 }}>
              <p>Crear oferta</p>
              <button
                type="button"
                className="secondary-btn"
                onClick={() => navigate("/recruiter/ofertas/nueva")}
              >
                Ir
              </button>
            </div>
            <div className="action-card" style={{ minWidth: 200 }}>
              <p>Postulaciones de postulantes</p>
              <button
                type="button"
                className="secondary-btn"
                onClick={() => navigate("/recruiter/postulaciones")}
              >
                Ir
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
