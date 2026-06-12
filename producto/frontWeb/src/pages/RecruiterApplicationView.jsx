import { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import EstablishMatchModal from "../components/EstablishMatchModal";
import { getJob, getJobApplication } from "../services/companyService";
import ApplicationMatchRow, {
  candidateFullName,
  matchLevelFromScore,
} from "../components/ApplicationMatchRow";
import "../styles/Home.css";

const CARD_FADE_MS = 360;
const CARD_FLIP_MS = 560;

const SCORE_DIMENSIONS = [
  { key: "description_score", label: "Descripción de la oferta" },
  { key: "title_score", label: "Título de la oferta" },
  { key: "modality_score", label: "Modalidad" },
  { key: "workload_score", label: "Carga horaria" },
  { key: "experience_score", label: "Años de experiencia" },
];

function wait(ms) {
  return new Promise((resolve) => {
    window.setTimeout(resolve, ms);
  });
}

function scoreCandidateName(preview) {
  if (!preview) return "Postulante";
  const parts = [preview.first_name, preview.last_name_paternal].filter(Boolean);
  return parts.length > 0 ? parts.join(" ") : "Postulante";
}

function isScorePath(pathname) {
  return pathname.endsWith("/score");
}

export default function RecruiterApplicationView() {
  const { jobId, applicationId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const scoreView = isScorePath(location.pathname);
  const [item, setItem] = useState(null);
  const [jobTitle, setJobTitle] = useState("");
  const [error, setError] = useState("");
  const [view, setView] = useState(scoreView ? "score" : "application");
  const [flipped, setFlipped] = useState(scoreView);
  const [isAnimating, setIsAnimating] = useState(false);
  const [appShown, setAppShown] = useState(!scoreView);
  const [scoreShown, setScoreShown] = useState(scoreView);
  const [matchModalOpen, setMatchModalOpen] = useState(false);
  const sceneRef = useRef(null);

  useEffect(() => {
    if (!jobId || !applicationId) return;
    let cancelled = false;
    Promise.all([getJobApplication(jobId, applicationId), getJob(jobId)])
      .then(([application, job]) => {
        if (cancelled) return;
        setJobTitle(job?.title || "");
        if (!application) {
          setError("Postulación no encontrada");
          setItem(null);
          return;
        }
        setItem(application);
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

  useEffect(() => {
    if (isAnimating) return;
    unlockSceneHeight();
    const onScore = isScorePath(location.pathname);
    setView(onScore ? "score" : "application");
    setFlipped(onScore);
    setAppShown(!onScore);
    setScoreShown(onScore);
  }, [location.pathname, isAnimating]);

  const lockSceneHeight = () => {
    const scene = sceneRef.current;
    if (!scene) return;
    scene.style.height = `${scene.offsetHeight}px`;
  };

  const unlockSceneHeight = () => {
    const scene = sceneRef.current;
    if (!scene) return;
    scene.style.height = "";
  };

  const switchToScore = async () => {
    if (isAnimating || isScorePath(location.pathname)) return;
    setIsAnimating(true);
    lockSceneHeight();
    setAppShown(false);
    await wait(CARD_FADE_MS);
    setFlipped(true);
    await wait(CARD_FLIP_MS);
    unlockSceneHeight();
    setView("score");
    navigate(
      `/recruiter/ofertas/${jobId}/postulaciones/${applicationId}/score`,
      { replace: true }
    );
    setScoreShown(false);
    await wait(16);
    setScoreShown(true);
    await wait(CARD_FADE_MS);
    setIsAnimating(false);
  };

  const switchToApplication = async () => {
    if (isAnimating || !isScorePath(location.pathname)) return;
    setIsAnimating(true);
    lockSceneHeight();
    setScoreShown(false);
    await wait(CARD_FADE_MS);
    setFlipped(false);
    await wait(CARD_FLIP_MS);
    unlockSceneHeight();
    setView("application");
    navigate(`/recruiter/ofertas/${jobId}/postulaciones/${applicationId}`, {
      replace: true,
    });
    setAppShown(false);
    await wait(16);
    setAppShown(true);
    await wait(CARD_FADE_MS);
    setIsAnimating(false);
  };

  const name = candidateFullName(item?.candidate_preview);
  const matchLevel = item ? matchLevelFromScore(item.match_score) : null;
  const breakdown = item?.match_breakdown;
  const finalScore =
    breakdown?.final_score != null
      ? breakdown.final_score
      : item?.match_score;

  return (
    <div>
      <Navbar />
      <div className="home-container home-container--job-detail">
        <div className="home-content recruiter-job-detail-shell">
          <div className="recruiter-job-detail-layout">
            <div className="recruiter-job-detail-layout__body">
              {(item || error) && (
                <div className="recruiter-job-detail-top-row recruiter-application-view-top-row">
                  <button
                    type="button"
                    className="secondary-btn recruiter-job-detail-top-btn"
                    onClick={() =>
                      isScorePath(location.pathname)
                        ? switchToApplication()
                        : navigate(`/recruiter/ofertas/${jobId}?postulaciones=1`)
                    }
                    disabled={isAnimating}
                  >
                    Volver
                  </button>
                </div>
              )}
              {error && <p className="recruiter-job-detail-error">{error}</p>}
              {item && (
                <div
                  ref={sceneRef}
                  className={`application-card-scene${
                    flipped ? " is-flipped" : ""
                  }${isAnimating ? " is-animating" : ""}`}
                >
                  <div className="application-card-flipper">
                    <div className="application-card-face application-card-face--front">
                      <article className="recruitment-card recruiter-job-detail-card recruiter-application-view-card">
                        <div
                          className="application-card-panel-content"
                          data-shown={appShown ? "true" : "false"}
                        >
                          <h2 className="recruiter-job-detail-title recruiter-application-view-title">
                            Postulación para {jobTitle}
                          </h2>
                          <div className="recruiter-application-view-body">
                            <div className="recruiter-application-candidate-header">
                              <p className="recruiter-application-candidate-name">
                                <strong>{name}</strong>
                              </p>
                              <div className="recruiter-application-header-actions">
                                <button
                                  type="button"
                                  className="recruiter-match-btn recruiter-establecer-match-btn"
                                  onClick={() => setMatchModalOpen(true)}
                                >
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
                                <button
                                  type="button"
                                  className="recruiter-ver-perfil-btn recruiter-ver-perfil-btn--active"
                                  onClick={() =>
                                    navigate(
                                      `/recruiter/ofertas/${jobId}/postulaciones/${applicationId}/perfil`
                                    )
                                  }
                                >
                                  Ver perfil
                                </button>
                              </div>
                            </div>
                            <div className="recruiter-application-view-meta">
                              {item.candidate_preview?.sector_name ? (
                                <p className="recruiter-application-view-sector">
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
                            </div>
                            <div className="recruiter-application-view-score">
                              <ApplicationMatchRow score={item.match_score} />
                            </div>
                            {item.match_explanation ? (
                              <p className="recruiter-application-view-explanation">
                                {item.match_explanation}
                              </p>
                            ) : null}
                            <div className="recruiter-application-view-footer">
                              <button
                                type="button"
                                className="primary-btn recruiter-application-view-score-btn"
                                onClick={switchToScore}
                                disabled={isAnimating}
                              >
                                Detalle de Score
                              </button>
                            </div>
                          </div>
                        </div>
                      </article>
                    </div>
                    <div className="application-card-face application-card-face--back">
                      <article className="recruitment-card recruiter-job-detail-card recruiter-score-detail-card">
                        <div
                          className="application-card-panel-content"
                          data-shown={scoreShown ? "true" : "false"}
                        >
                          <h2 className="recruiter-job-detail-title recruiter-score-detail-title">
                            Detalle de Score
                          </h2>
                          <div className="recruiter-score-detail-body">
                            <p className="recruiter-score-detail-candidate">
                              {scoreCandidateName(item.candidate_preview)}
                            </p>
                            {finalScore != null ? (
                              <p className="recruiter-score-detail-final">
                                Score final:{" "}
                                <strong>{Math.round(finalScore)}%</strong>
                              </p>
                            ) : (
                              <p className="recruiter-score-detail-unavailable">
                                No hay score disponible para esta postulación.
                              </p>
                            )}
                            {breakdown ? (
                              <div className="recruiter-score-detail-breakdown">
                                {SCORE_DIMENSIONS.map(({ key, label }) => {
                                  const val = breakdown[key];
                                  const pct =
                                    val != null ? Math.round(val) : 0;
                                  return (
                                    <div
                                      key={key}
                                      className="recruiter-score-detail-dimension"
                                    >
                                      <div className="recruiter-score-detail-dimension-head">
                                        <span>{label}</span>
                                        <strong>{pct}%</strong>
                                      </div>
                                      <div className="score-bar-track recruiter-score-detail-bar">
                                        <div
                                          className="score-bar-fill"
                                          style={{
                                            width: `${Math.min(100, Math.max(0, pct))}%`,
                                          }}
                                        />
                                      </div>
                                    </div>
                                  );
                                })}
                              </div>
                            ) : finalScore != null ? (
                              <p className="recruiter-score-detail-no-breakdown">
                                Desglose por dimensión no disponible para
                                postulaciones anteriores.
                              </p>
                            ) : null}
                          </div>
                        </div>
                      </article>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
      <EstablishMatchModal
        open={matchModalOpen}
        candidateName={name}
        applicationId={applicationId}
        onClose={() => setMatchModalOpen(false)}
        onGoToMessages={(conversationId) =>
          navigate(`/recruiter/matchs/${conversationId}`)
        }
      />
    </div>
  );
}
