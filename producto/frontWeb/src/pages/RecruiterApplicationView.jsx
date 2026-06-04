import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import { listJobApplications } from "../services/companyService";
import ApplicationMatchRow, {
  candidateFullName,
  matchLevelFromScore,
} from "../components/ApplicationMatchRow";
import "../styles/Home.css";

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

  const name = candidateFullName(item?.candidate_preview);
  const matchLevel = item ? matchLevelFromScore(item.match_score) : null;

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
                navigate(`/recruiter/ofertas/${jobId}?postulaciones=1`)
              }
            >
              Volver
            </button>
          </div>
          {error && <p className="recruiter-job-detail-error">{error}</p>}
          {item && (
            <article className="recruitment-card recruiter-job-detail-card">
              <h2 className="recruiter-job-detail-title">Ver postulación</h2>
              <div className="recruiter-application-candidate-header">
                <p className="recruiter-job-detail-company recruiter-application-candidate-name">
                  <strong>{name}</strong>
                </p>
                <div className="recruiter-application-header-actions">
                  <button type="button" className="recruiter-match-btn">
                    <span>Match</span>
                    <svg
                      className="recruiter-match-btn__star"
                      viewBox="0 0 24 24"
                      aria-hidden="true"
                    >
                      <path
                        fill="currentColor"
                        d="M12 2l2.9 6.26 6.84.56-5.18 4.48 1.56 6.7L12 17.77l-6.12 3.23 1.56-6.7-5.18-4.48 6.84-.56L12 2z"
                      />
                    </svg>
                  </button>
                  <button type="button" className="recruiter-ver-perfil-btn">
                    Ver perfil
                  </button>
                </div>
              </div>
              {item.candidate_preview?.sector_name ? (
                <p style={{ color: "#64748b", margin: "0 0 12px" }}>
                  Rubro: {item.candidate_preview.sector_name}
                </p>
              ) : null}
              {matchLevel ? (
                <p
                  className={`match-level-label match-level-label--${matchLevel.tone}`}
                >
                  {matchLevel.text}
                </p>
              ) : null}
              <ApplicationMatchRow score={item.match_score} />
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
