import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import { listJobApplications } from "../services/companyService";
import "../styles/Home.css";

const DIMENSIONS = [
  { key: "description_score", label: "Descripción de la oferta" },
  { key: "title_score", label: "Título de la oferta" },
  { key: "modality_score", label: "Modalidad" },
  { key: "workload_score", label: "Carga horaria" },
  { key: "experience_score", label: "Años de experiencia" },
];

function candidateName(preview) {
  if (!preview) return "Postulante";
  const parts = [preview.first_name, preview.last_name_paternal].filter(Boolean);
  return parts.length > 0 ? parts.join(" ") : "Postulante";
}

export default function RecruiterMatchScoreDetail() {
  const { jobId, applicationId } = useParams();
  const navigate = useNavigate();
  const [item, setItem] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!jobId || !applicationId) return;
    let cancelled = false;
    listJobApplications(jobId, 1, 100)
      .then((data) => {
        if (cancelled) return;
        const found = (data.items || []).find(
          (x) => String(x.application_id) === String(applicationId)
        );
        if (!found) {
          setError("Postulación no encontrada");
          setItem(null);
          return;
        }
        setItem(found);
        setError("");
      })
      .catch((err) => {
        if (!cancelled) {
          setError(
            err.response?.data?.error?.message ||
              "No se pudo cargar el detalle de score"
          );
        }
      });
    return () => {
      cancelled = true;
    };
  }, [jobId, applicationId]);

  const breakdown = item?.match_breakdown;
  const finalScore =
    breakdown?.final_score != null
      ? breakdown.final_score
      : item?.match_score;

  return (
    <div>
      <Navbar />
      <div className="home-container">
        <div className="home-content recruiter-job-detail-shell">
          <div className="recruiter-job-detail-toolbar">
            <button
              type="button"
              className="secondary-btn recruiter-job-detail-toolbar-btn"
              onClick={() =>
                navigate(
                  `/recruiter/ofertas/${jobId}/postulaciones/${applicationId}`
                )
              }
            >
              Volver
            </button>
          </div>
          {error && <p className="recruiter-job-detail-error">{error}</p>}
          {item && (
            <article className="recruitment-card recruiter-job-detail-card">
              <h2 className="recruiter-job-detail-title">Detalle de Score</h2>
              <p className="recruiter-job-detail-company">
                {candidateName(item.candidate_preview)}
              </p>
              {finalScore != null ? (
                <p style={{ fontSize: "1.25rem", margin: "16px 0" }}>
                  Score final: <strong>{Math.round(finalScore)}%</strong>
                </p>
              ) : (
                <p style={{ color: "#b91c1c" }}>
                  No hay score disponible para esta postulación.
                </p>
              )}
              {breakdown ? (
                <div style={{ marginTop: 20 }}>
                  {DIMENSIONS.map(({ key, label }) => {
                    const val = breakdown[key];
                    const pct = val != null ? Math.round(val) : 0;
                    return (
                      <div key={key} style={{ marginBottom: 16 }}>
                        <div
                          style={{
                            display: "flex",
                            justifyContent: "space-between",
                            marginBottom: 4,
                            fontSize: 14,
                          }}
                        >
                          <span>{label}</span>
                          <strong>{pct}%</strong>
                        </div>
                        <div className="score-bar-track">
                          <div
                            className="score-bar-fill"
                            style={{ width: `${Math.min(100, Math.max(0, pct))}%` }}
                          />
                        </div>
                      </div>
                    );
                  })}
                </div>
              ) : finalScore != null ? (
                <p style={{ color: "#64748b" }}>
                  Desglose por dimensión no disponible para postulaciones
                  anteriores.
                </p>
              ) : null}
            </article>
          )}
        </div>
      </div>
    </div>
  );
}
