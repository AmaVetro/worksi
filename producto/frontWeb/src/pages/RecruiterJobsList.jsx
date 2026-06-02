import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import ApplicationsCountChip from "../components/ApplicationsCountChip";
import { listMyJobs } from "../services/companyService";
import { goBack } from "../utils/goBack";
import "../styles/Home.css";

export default function RecruiterJobsList() {
  const navigate = useNavigate();
  const [items, setItems] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    listMyJobs(1, 50)
      .then((data) => setItems(data.items || []))
      .catch((err) => {
        setError(
          err.response?.data?.error?.message || "No se pudo cargar el listado"
        );
        setItems([]);
      });
  }, []);

  return (
    <div>
      <Navbar />
      <div className="home-container">
        <div className="home-content">
          <div style={{ marginBottom: 16 }}>
            <button
              type="button"
              className="secondary-btn"
              style={{ marginTop: 0, marginBottom: 12 }}
              onClick={() => goBack(navigate)}
            >
              Volver
            </button>
            <h2 style={{ margin: 0, color: "#0f766e" }}>Ofertas publicadas</h2>
          </div>
          {error && <p style={{ color: "#b91c1c" }}>{error}</p>}
          <div className="recruitment-card" style={{ marginBottom: 12 }}>
            {items.length === 0 && !error && (
              <p style={{ color: "#64748b" }}>No hay ofertas aún.</p>
            )}
            {items.map((job) => (
              <div
                key={job.id}
                style={{
                  borderBottom: "1px solid #eee",
                  padding: "12px 0",
                  display: "flex",
                  justifyContent: "space-between",
                  gap: 12,
                  flexWrap: "wrap",
                }}
              >
                <div>
                  <strong>{job.title}</strong>
                  <div style={{ fontSize: 13, color: "#64748b" }}>
                    {job.company_commercial_name} · ${job.salary_offered} ·{" "}
                    {job.modality}
                  </div>
                </div>
                <div
                  style={{
                    display: "flex",
                    alignItems: "center",
                    gap: 12,
                    flexWrap: "wrap",
                  }}
                >
                  <ApplicationsCountChip count={job.applications_count} />
                  <button
                  type="button"
                  className="secondary-btn"
                  style={{ marginTop: 0, alignSelf: "center" }}
                  onClick={() => navigate(`/recruiter/ofertas/${job.id}`)}
                >
                  Ir a ver oferta
                </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
