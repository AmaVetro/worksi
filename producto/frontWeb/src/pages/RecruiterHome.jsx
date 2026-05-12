import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { listMyJobs } from "../services/companyService";
import "../styles/Home.css";

function readUser() {
  try {
    return JSON.parse(localStorage.getItem("user") || "{}");
  } catch {
    return {};
  }
}

export default function RecruiterHome() {
  const navigate = useNavigate();
  const user = readUser();
  const [jobsTotal, setJobsTotal] = useState(0);
  const [companyLabel, setCompanyLabel] = useState("");

  useEffect(() => {
    listMyJobs(1, 20)
      .then((data) => {
        setJobsTotal(data.total_items ?? 0);
        const first = (data.items || [])[0];
        if (first && first.company_commercial_name) {
          setCompanyLabel(first.company_commercial_name);
        }
      })
      .catch(() => {
        setJobsTotal(0);
      });
  }, []);

  return (
    <div>
      <Navbar />
      <div className="home-container">
        <div className="home-content">
          <div className="user-card" style={{ maxWidth: "100%" }}>
            <div className="user-logo">Empresa</div>
            <div>
              <strong>{companyLabel || "—"}</strong>
              <p style={{ margin: "6px 0 0", color: "#555" }}>
                {user.email} — Reclutador
              </p>
            </div>
          </div>

          <div className="home-grid">
            <div className="recruitment-card">
              <div className="card-header">
                <h3>Reclutamiento</h3>
                <span onClick={() => navigate("/recruiter/reclutamiento")}>
                  Ir a módulo
                </span>
              </div>
              <div className="card-content">
                <p>
                  Ofertas publicadas
                  <span className="dots"></span>
                  <strong>{jobsTotal}</strong>
                </p>
              </div>
              <button
                type="button"
                className="primary-btn"
                onClick={() => navigate("/recruiter/ofertas")}
              >
                Ir a ver
              </button>
            </div>

            <div className="actions-container">
              <div className="action-card">
                <p>Postulaciones</p>
                <button
                  type="button"
                  className="secondary-btn"
                  onClick={() => navigate("/recruiter/postulaciones")}
                >
                  Ver postulaciones
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
