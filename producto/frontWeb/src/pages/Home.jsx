import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { getActiveJobsTotal, getSystemStatus } from "../services/adminService";
import loginImage from "../assets/images/login-bg.jpg";
import "../styles/Home.css";

const STATUS_ITEMS = [
  { key: "backend", label: "Backend" },
  { key: "database", label: "Base de datos" },
  { key: "ai", label: "Servicio IA" },
];

function Home() {
  const navigate = useNavigate();
  const [jobsTotal, setJobsTotal] = useState(null);
  const [systemStatus, setSystemStatus] = useState(null);
  const [statusLoading, setStatusLoading] = useState(true);
  const [statusError, setStatusError] = useState("");

  const loadSystemStatus = useCallback(() => {
    setStatusLoading(true);
    setStatusError("");
    return getSystemStatus()
      .then((data) => setSystemStatus(data))
      .catch((err) => {
        setSystemStatus(null);
        setStatusError(
          err.response?.data?.error?.message ||
            "No se pudo consultar el estado del sistema"
        );
      })
      .finally(() => setStatusLoading(false));
  }, []);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const total = await getActiveJobsTotal();
        if (!cancelled) {
          setJobsTotal(total);
        }
      } catch {
        if (!cancelled) {
          setJobsTotal(0);
        }
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    loadSystemStatus();
  }, [loadSystemStatus]);

  const jobsLabel = jobsTotal === null ? "…" : String(jobsTotal);

  return (
    <div>
      <Navbar />

      <div className="home-container">
        <div className="home-content">
          <div className="recruiter-home-banner">
            <img src={loginImage} alt="WorkSí" />
          </div>

          <div className="home-grid">
            <div className="recruitment-card">
              <div className="card-header">
                <h3>Reclutamiento</h3>
              </div>

              <div className="card-content">
                <p>
                  Ofertas Publicadas
                  <span className="dots"></span>
                  <strong>{jobsLabel}</strong>
                </p>
              </div>

              <button
                type="button"
                className="primary-btn"
                onClick={() => navigate("/ofertas")}
              >
                Gestionar Ofertas
              </button>
            </div>

            <div className="admin-system-status-card">
              <div className="card-header admin-system-status-header">
                <h3>Estado del sistema</h3>
                <button
                  type="button"
                  className="admin-system-status-refresh"
                  onClick={loadSystemStatus}
                  disabled={statusLoading}
                >
                  {statusLoading ? "Actualizando…" : "Actualizar"}
                </button>
              </div>

              {statusError && (
                <p className="admin-system-status-error">{statusError}</p>
              )}

              <ul className="admin-system-status-list">
                {STATUS_ITEMS.map(({ key, label }) => {
                  const value = systemStatus?.[key];
                  const up = value === "UP";
                  const pending = statusLoading && !statusError;
                  return (
                    <li key={key} className="admin-system-status-row">
                      <span
                        className={`admin-system-status-dot ${
                          pending
                            ? "admin-system-status-dot--pending"
                            : up
                              ? "admin-system-status-dot--up"
                              : "admin-system-status-dot--down"
                        }`}
                        aria-hidden="true"
                      />
                      <span className="admin-system-status-label">{label}</span>
                      <span className="dots" />
                      <strong className="admin-system-status-value">
                        {pending ? "…" : up ? "Operativo" : "No disponible"}
                      </strong>
                    </li>
                  );
                })}
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Home;
