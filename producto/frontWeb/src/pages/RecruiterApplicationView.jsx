import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import { listJobApplications } from "../services/companyService";
import { goBack } from "../utils/goBack";
import "../styles/Home.css";

function candidateName(preview) {
  if (!preview) return "Postulante";
  const parts = [preview.first_name, preview.last_name_paternal].filter(Boolean);
  return parts.length > 0 ? parts.join(" ") : "Postulante";
}

export default function RecruiterApplicationView() {
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
              "No se pudo cargar la postulación"
          );
        }
      });
    return () => {
      cancelled = true;
    };
  }, [jobId, applicationId]);

  const name = candidateName(item?.candidate_preview);
  const score =
    item?.match_score != null ? `${Math.round(item.match_score)}%` : "—";

  return (
    <div>
      <Navbar />
      <div className="home-container">
        <div className="home-content recruiter-job-detail-shell">
          <div className="recruiter-job-detail-toolbar">
            <button
              type="button"
              className="secondary-btn recruiter-job-detail-toolbar-btn"
              onClick={() => goBack(navigate)}
            >
              Volver
            </button>
          </div>
          {error && <p className="recruiter-job-detail-error">{error}</p>}
          {item && (
            <article className="recruitment-card recruiter-job-detail-card">
              <h2 className="recruiter-job-detail-title">Ver postulación</h2>
              <p className="recruiter-job-detail-company">
                <strong>{name}</strong>
              </p>
              {item.candidate_preview?.sector_name ? (
                <p style={{ color: "#64748b", margin: "0 0 12px" }}>
                  Rubro: {item.candidate_preview.sector_name}
                </p>
              ) : null}
              <p style={{ margin: "8px 0" }}>
                Estado: <strong>{item.status}</strong>
              </p>
              <p style={{ margin: "8px 0" }}>
                Compatibilidad: <strong>{score}</strong>
              </p>
              {item.match_explanation ? (
                <p
                  style={{
                    margin: "12px 0",
                    color: "#475569",
                    lineHeight: 1.5,
                  }}
                >
                  {item.match_explanation}
                </p>
              ) : null}
              <div style={{ display: "flex", gap: 12, flexWrap: "wrap", marginTop: 20 }}>
                <button
                  type="button"
                  className="primary-btn"
                  onClick={() =>
                    navigate(
                      `/recruiter/ofertas/${jobId}/postulaciones/${applicationId}/score`
                    )
                  }
                >
                  Detalle de Score
                </button>
              </div>
            </article>
          )}
        </div>
      </div>
    </div>
  );
}
