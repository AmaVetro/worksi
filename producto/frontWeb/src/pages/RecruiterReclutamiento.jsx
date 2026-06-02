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
          <div className="actions-container recruiter-reclutamiento-actions">
            <div className="action-card recruiter-reclutamiento-card">
              <p className="recruiter-reclutamiento-card-title">
                Ofertas publicadas
              </p>
              <button
                type="button"
                className="secondary-btn"
                onClick={() => navigate("/recruiter/ofertas")}
              >
                Ir
              </button>
            </div>
            <div className="action-card recruiter-reclutamiento-card">
              <p className="recruiter-reclutamiento-card-title">
                Crear
                <br />
                oferta
              </p>
              <button
                type="button"
                className="secondary-btn"
                onClick={() => navigate("/recruiter/ofertas/nueva")}
              >
                Ir
              </button>
            </div>
            <div className="action-card recruiter-reclutamiento-card">
              <p className="recruiter-reclutamiento-card-title">
                Postulaciones por oferta
              </p>
              <button
                type="button"
                className="secondary-btn"
                onClick={() => navigate("/recruiter/ofertas")}
              >
                Ir a ofertas
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
