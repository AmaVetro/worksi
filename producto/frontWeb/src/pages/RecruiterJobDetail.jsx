import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import ConfirmModal from "../components/ConfirmModal";
import { ViewApplicationsButton } from "../components/ApplicationsCountChip";
import ApplicationMatchRow, { candidateFullName } from "../components/ApplicationMatchRow";
import {
  deleteJob,
  getJob,
  getJobImageBlob,
  listJobApplications,
  patchJobStatus,
} from "../services/companyService";
import { formatJobDateDisplay } from "../utils/formatJobDate";
import "../styles/Home.css";

const MODALITY_LABELS = {
  REMOTE: "Remoto",
  HYBRID: "Híbrido",
  ONSITE: "Presencial",
};

const WORKLOAD_LABELS = {
  FULL_TIME: "Full time",
  PART_TIME: "Part time",
  OTHER: "Otro",
};

const CARD_FADE_MS = 360;
const CARD_RESIZE_MS = 480;
const POSTULATIONS_CARD_WIDTH = 720;

function wait(ms) {
  return new Promise((resolve) => {
    window.setTimeout(resolve, ms);
  });
}

function measurePanelSize(panelEl, cardEl) {
  if (!panelEl || !cardEl) return null;

  const cardStyle = window.getComputedStyle(cardEl);
  const padX =
    parseFloat(cardStyle.paddingLeft) + parseFloat(cardStyle.paddingRight);
  const padY =
    parseFloat(cardStyle.paddingTop) + parseFloat(cardStyle.paddingBottom);

  const prevDisplay = panelEl.style.display;
  const prevVisibility = panelEl.style.visibility;
  const prevPosition = panelEl.style.position;
  const prevWidth = panelEl.style.width;

  panelEl.style.display = "block";
  panelEl.style.visibility = "hidden";
  panelEl.style.position = "absolute";
  panelEl.style.width = "max-content";

  const width = panelEl.offsetWidth + padX;
  const height = panelEl.offsetHeight + padY;

  panelEl.style.display = prevDisplay;
  panelEl.style.visibility = prevVisibility;
  panelEl.style.position = prevPosition;
  panelEl.style.width = prevWidth;

  return { width, height };
}

function PowerIcon() {
  return (
    <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
      <path
        fill="none"
        stroke="currentColor"
        strokeWidth="2.75"
        strokeLinecap="round"
        d="M12 3v8"
      />
      <path
        fill="none"
        stroke="currentColor"
        strokeWidth="2.75"
        strokeLinecap="round"
        d="M7.2 7.2a7 7 0 1 0 9.6 0"
      />
    </svg>
  );
}

function DeleteIcon() {
  return (
    <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
      <path
        fill="none"
        stroke="currentColor"
        strokeWidth="2.5"
        strokeLinecap="round"
        d="M6 6l12 12M18 6L6 18"
      />
    </svg>
  );
}

export default function RecruiterJobDetail() {
  const { jobId } = useParams();
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const [job, setJob] = useState(null);
  const [error, setError] = useState("");
  const [imageSrc, setImageSrc] = useState(null);
  const [view, setView] = useState("detail");
  const [isAnimating, setIsAnimating] = useState(false);
  const [detailMounted, setDetailMounted] = useState(true);
  const [appsMounted, setAppsMounted] = useState(false);
  const [detailShown, setDetailShown] = useState(true);
  const [appsShown, setAppsShown] = useState(false);
  const [editVisible, setEditVisible] = useState(true);
  const [editShown, setEditShown] = useState(true);
  const [listBackVisible, setListBackVisible] = useState(true);
  const [listBackShown, setListBackShown] = useState(true);
  const [detailBackVisible, setDetailBackVisible] = useState(false);
  const [detailBackShown, setDetailBackShown] = useState(false);
  const [detailLeaving, setDetailLeaving] = useState(false);
  const [appsLeaving, setAppsLeaving] = useState(false);
  const [applications, setApplications] = useState([]);
  const [appsError, setAppsError] = useState("");
  const [lifecycleBusy, setLifecycleBusy] = useState(false);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const blobUrlRef = useRef(null);
  const sceneRef = useRef(null);
  const cardRef = useRef(null);
  const detailPanelRef = useRef(null);
  const appsPanelRef = useRef(null);
  const initialUrlSyncDone = useRef(false);

  useEffect(() => {
    if (!jobId) return;
    let cancelled = false;
    const revoke = () => {
      if (blobUrlRef.current) {
        URL.revokeObjectURL(blobUrlRef.current);
        blobUrlRef.current = null;
      }
    };
    setImageSrc(null);
    revoke();
    getJob(jobId)
      .then(async (j) => {
        if (cancelled) return;
        setJob(j);
        if (j.external_image_url) {
          setImageSrc(j.external_image_url);
          return;
        }
        if (j.has_protected_image) {
          try {
            const blob = await getJobImageBlob(jobId);
            if (cancelled) return;
            if (blob && blob.type && blob.type.startsWith("image/")) {
              const u = URL.createObjectURL(blob);
              blobUrlRef.current = u;
              setImageSrc(u);
            }
          } catch {
            if (!cancelled) setImageSrc(null);
          }
        }
      })
      .catch((err) => {
        if (!cancelled) {
          setError(
            err.response?.data?.error?.message || "No se pudo cargar la oferta"
          );
        }
      });
    return () => {
      cancelled = true;
      revoke();
    };
  }, [jobId]);

  useEffect(() => {
    if (!jobId || initialUrlSyncDone.current) return;
    if (searchParams.has("postulaciones")) {
      initialUrlSyncDone.current = true;
      setView("applications");
      setDetailMounted(false);
      setDetailShown(false);
      setAppsMounted(true);
      setAppsShown(true);
      setEditVisible(false);
      setEditShown(false);
      setListBackVisible(false);
      setListBackShown(false);
      setDetailBackVisible(true);
      setDetailBackShown(true);
      loadApplications();
    }
  }, [jobId, searchParams]);

  const loadApplications = () => {
    if (!jobId) return Promise.resolve();
    setAppsError("");
    return listJobApplications(jobId, 1, 50)
      .then((data) => setApplications(data.items || []))
      .catch((err) => {
        setApplications([]);
        setAppsError(
          err.response?.data?.error?.message ||
            "No se pudieron cargar las postulaciones"
        );
      });
  };

  const lockSceneSize = () => {
    const scene = sceneRef.current;
    if (!scene) return;
    scene.style.width = `${scene.offsetWidth}px`;
    scene.style.height = `${scene.offsetHeight}px`;
  };

  const animateSceneSizeTo = async (targetWidth, targetHeight) => {
    const scene = sceneRef.current;
    if (!scene) return;
    scene.style.transition = `width ${CARD_RESIZE_MS}ms cubic-bezier(0.4, 0, 0.2, 1), height ${CARD_RESIZE_MS}ms cubic-bezier(0.4, 0, 0.2, 1)`;
    await wait(16);
    scene.style.width = `${targetWidth}px`;
    scene.style.height = `${targetHeight}px`;
    await wait(CARD_RESIZE_MS);
  };

  const unlockSceneSize = () => {
    const scene = sceneRef.current;
    if (!scene) return;
    scene.style.width = "";
    scene.style.height = "";
    scene.style.transition = "";
  };

  const switchToApplications = async () => {
    if (isAnimating || view === "applications") return;
    setIsAnimating(true);
    lockSceneSize();
    setDetailLeaving(true);
    setDetailShown(false);
    setEditShown(false);
    setListBackShown(false);
    await wait(CARD_FADE_MS);
    setListBackVisible(false);
    setDetailLeaving(false);
    setEditVisible(false);
    setDetailMounted(false);
    setView("applications");
    setSearchParams({ postulaciones: "1" }, { replace: true });
    setAppsMounted(true);
    setAppsShown(false);
    await loadApplications();
    await wait(32);
    const size = measurePanelSize(appsPanelRef.current, cardRef.current);
    if (size) {
      await animateSceneSizeTo(POSTULATIONS_CARD_WIDTH, size.height);
    }
    setDetailBackVisible(true);
    setDetailBackShown(false);
    await wait(16);
    setDetailBackShown(true);
    setAppsShown(true);
    await wait(CARD_FADE_MS);
    unlockSceneSize();
    setIsAnimating(false);
  };

  const switchToDetail = async () => {
    if (isAnimating || view === "detail") return;
    setIsAnimating(true);
    lockSceneSize();
    setAppsLeaving(true);
    setAppsShown(false);
    setDetailBackShown(false);
    await wait(CARD_FADE_MS);
    setDetailBackVisible(false);
    setAppsLeaving(false);
    setAppsMounted(false);
    setView("detail");
    setSearchParams({}, { replace: true });
    setDetailMounted(true);
    setDetailShown(false);
    setEditVisible(false);
    setEditShown(false);
    await wait(32);
    const size = measurePanelSize(detailPanelRef.current, cardRef.current);
    if (size) {
      await animateSceneSizeTo(size.width, size.height);
    }
    setListBackVisible(true);
    setListBackShown(false);
    await wait(16);
    setListBackShown(true);
    setEditVisible(true);
    setDetailShown(true);
    setEditShown(true);
    await wait(CARD_FADE_MS);
    unlockSceneSize();
    setIsAnimating(false);
  };

  const handleFlip = () => {
    switchToApplications();
  };

  const backFromApplicationsList = () => {
    switchToDetail();
  };

  const reloadJob = () => {
    if (!jobId) return;
    getJob(jobId)
      .then((j) => setJob(j))
      .catch((err) => {
        setError(
          err.response?.data?.error?.message || "No se pudo cargar la oferta"
        );
      });
  };

  const handleToggleStatus = async () => {
    if (!job?.id || lifecycleBusy) return;
    const nextStatus = job.status === "INACTIVE" ? "ACTIVE" : "INACTIVE";
    setLifecycleBusy(true);
    try {
      await patchJobStatus(job.id, nextStatus);
      reloadJob();
    } catch (err) {
      setError(
        err.response?.data?.error?.message ||
          "No se pudo cambiar el estado de la oferta"
      );
    } finally {
      setLifecycleBusy(false);
    }
  };

  const confirmDelete = async () => {
    if (!job?.id || lifecycleBusy) return;
    setLifecycleBusy(true);
    try {
      await deleteJob(job.id);
      setDeleteModalOpen(false);
      navigate("/recruiter/ofertas");
    } catch (err) {
      setError(
        err.response?.data?.error?.message || "No se pudo eliminar la oferta"
      );
      setLifecycleBusy(false);
    }
  };

  const regionLine = job
    ? job.region_name
      ? job.region_name
      : `Región id: ${job.region_id}`
    : "";
  const communeLine = job
    ? job.commune_name
      ? job.commune_name
      : `Comuna id: ${job.commune_id}`
    : "";
  const appsCount = job?.applications_count ?? 0;

  const skillsList =
    job && Array.isArray(job.skills) && job.skills.length > 0
      ? job.skills
      : (job?.skills_ids || []).map((id) => ({ id, name: `Skill #${id}` }));

  return (
    <div>
      <Navbar />
      <div className="home-container home-container--job-detail">
        <div className="home-content recruiter-job-detail-shell">
          <div className="recruiter-job-detail-layout">
            <div className="recruiter-job-detail-layout__body">
              <div className="recruiter-job-detail-top-row">
                <div className="recruiter-job-detail-top-back">
                  {listBackVisible && (
                    <button
                      type="button"
                      className="secondary-btn recruiter-job-detail-top-btn recruiter-job-detail-top-btn--fade"
                      data-shown={listBackShown ? "true" : "false"}
                      onClick={() => navigate("/recruiter/ofertas")}
                      disabled={isAnimating}
                    >
                      Volver al listado
                    </button>
                  )}
                  {detailBackVisible && (
                    <button
                      type="button"
                      className="secondary-btn recruiter-job-detail-top-btn recruiter-job-detail-top-btn--fade"
                      data-shown={detailBackShown ? "true" : "false"}
                      onClick={backFromApplicationsList}
                      disabled={isAnimating}
                    >
                      Volver al detalle
                    </button>
                  )}
                </div>
                {!error && jobId && job && editVisible && (
                  <button
                    type="button"
                    className="secondary-btn recruiter-job-detail-top-btn recruiter-job-detail-top-btn--edit"
                    data-shown={editShown ? "true" : "false"}
                    onClick={() => navigate(`/recruiter/ofertas/${jobId}/editar`)}
                    disabled={isAnimating}
                  >
                    Editar oferta
                  </button>
                )}
              </div>
              {error && <p className="recruiter-job-detail-error">{error}</p>}
              {job && (
                <div
                  ref={sceneRef}
                  className={`job-card-scene ${
                    view === "applications" ? "is-applications" : "is-detail"
                  }${isAnimating ? " is-animating" : ""}`}
                >
                  <article
                    ref={cardRef}
                    className={`recruitment-card recruiter-job-detail-card${
                      view === "applications" ? " recruiter-job-detail-card--applications" : ""
                    }`}
                  >
                    {detailMounted && (
                      <div
                        ref={detailPanelRef}
                        className={`job-card-panel job-card-panel--detail${
                          detailShown || detailLeaving ? "" : " job-card-panel--inert"
                        }`}
                        data-shown={detailShown ? "true" : "false"}
                      >
                  <div className="recruiter-job-detail-horiz">
                    <div className="recruiter-job-detail-horiz__media">
                      {imageSrc ? (
                        <img
                          className="recruiter-job-detail-image"
                          src={imageSrc}
                          alt=""
                        />
                      ) : (
                        <div
                          className="recruiter-job-detail-image recruiter-job-detail-image--placeholder"
                          aria-hidden="true"
                        />
                      )}
                    </div>
                    <div className="recruiter-job-detail-horiz__main">
                      <h2 className="recruiter-job-detail-title">{job.title}</h2>
                      <p className="recruiter-job-detail-company">
                        {job.company_commercial_name}
                      </p>
                      <p className="recruiter-job-detail-description">
                        {job.description}
                      </p>
                    </div>
                    <div className="recruiter-job-detail-horiz__aside">
                      <section
                        className="recruiter-job-detail-meta"
                        aria-label="Detalles de la oferta"
                      >
                        <div className="recruiter-job-detail-meta-block">
                          <span className="recruiter-job-detail-meta-label">
                            Región
                          </span>
                          <span className="recruiter-job-detail-meta-value">
                            {regionLine}
                          </span>
                        </div>
                        <div className="recruiter-job-detail-meta-block">
                          <span className="recruiter-job-detail-meta-label">
                            Comuna
                          </span>
                          <span className="recruiter-job-detail-meta-value">
                            {communeLine}
                          </span>
                        </div>
                        <div className="recruiter-job-detail-meta-block">
                          <span className="recruiter-job-detail-meta-label">
                            Sueldo ofrecido
                          </span>
                          <span className="recruiter-job-detail-meta-value">
                            ${job.salary_offered}
                          </span>
                        </div>
                        <div className="recruiter-job-detail-meta-block">
                          <span className="recruiter-job-detail-meta-label">
                            Años de experiencia requeridos
                          </span>
                          <span className="recruiter-job-detail-meta-value">
                            {job.years_experience_required}
                          </span>
                        </div>
                        <div className="recruiter-job-detail-meta-block">
                          <span className="recruiter-job-detail-meta-label">
                            Modalidad
                          </span>
                          <span className="recruiter-job-detail-meta-value">
                            {MODALITY_LABELS[job.modality] ?? job.modality}
                          </span>
                        </div>
                        <div className="recruiter-job-detail-meta-block">
                          <span className="recruiter-job-detail-meta-label">
                            Carga horaria
                          </span>
                          <span className="recruiter-job-detail-meta-value">
                            {WORKLOAD_LABELS[job.workload] ?? job.workload}
                          </span>
                        </div>
                      </section>
                      <section className="recruiter-job-detail-skills-section">
                        <strong className="recruiter-job-detail-skills-title">
                          Skills
                        </strong>
                        {skillsList.length === 0 ? (
                          <p className="recruiter-job-detail-skills-empty">
                            Sin skills
                          </p>
                        ) : (
                          <ul className="recruiter-job-detail-skills-list">
                            {skillsList.map((s) => (
                              <li key={String(s.id)}>
                                <span className="recruiter-job-detail-skill-pill">
                                  {s.name?.trim() ? s.name : `Skill #${s.id}`}
                                </span>
                              </li>
                            ))}
                          </ul>
                        )}
                      </section>
                    </div>
                  </div>
                  <div className="recruiter-job-detail-card-footer">
                    <div className="recruiter-job-detail-footer-actions">
                      <ViewApplicationsButton
                        count={appsCount}
                        onClick={handleFlip}
                        disabled={isAnimating}
                      />
                      <button
                        type="button"
                        className={`recruiter-job-power-btn${
                          job.status === "INACTIVE"
                            ? " recruiter-job-power-btn--inactive"
                            : " recruiter-job-power-btn--active"
                        }`}
                        onClick={handleToggleStatus}
                        disabled={lifecycleBusy}
                        aria-label={
                          job.status === "INACTIVE"
                            ? "Activar oferta"
                            : "Desactivar oferta"
                        }
                      >
                        <PowerIcon />
                      </button>
                      <button
                        type="button"
                        className="recruiter-job-delete-btn"
                        onClick={() => setDeleteModalOpen(true)}
                        disabled={lifecycleBusy}
                        aria-label="Eliminar oferta"
                      >
                        <DeleteIcon />
                      </button>
                    </div>
                    <div className="recruiter-job-detail-dates">
                      <p>
                        Fecha creación: {formatJobDateDisplay(job.created_at)}
                      </p>
                      <p>
                        Fecha cierre: {formatJobDateDisplay(job.closing_date)}
                      </p>
                    </div>
                  </div>
                      </div>
                    )}

                    {appsMounted && (
                      <div
                        ref={appsPanelRef}
                        className={`job-card-panel job-card-panel--applications${
                          appsShown || appsLeaving ? "" : " job-card-panel--inert"
                        }`}
                        data-shown={appsShown ? "true" : "false"}
                      >
                        <h2 className="recruiter-job-detail-title">Postulaciones</h2>
                        {appsError && (
                          <p className="recruiter-job-detail-error">{appsError}</p>
                        )}
                        {applications.length === 0 && !appsError && (
                          <p className="recruiter-job-detail-apps-empty">
                            No hay postulaciones para esta oferta.
                          </p>
                        )}
                        <ul className="applications-back-list">
                          {applications.map((app) => (
                            <li key={app.application_id} className="applications-back-row">
                              <div className="applications-back-row-main">
                                <strong className="applications-back-name">
                                  {candidateFullName(app.candidate_preview)}
                                </strong>
                                <ApplicationMatchRow score={app.match_score} />
                              </div>
                              <button
                                type="button"
                                className="secondary-btn"
                                style={{ marginTop: 0 }}
                                onClick={() =>
                                  navigate(
                                    `/recruiter/ofertas/${jobId}/postulaciones/${app.application_id}`
                                  )
                                }
                              >
                                Ver postulación
                              </button>
                            </li>
                          ))}
                        </ul>
                      </div>
                    )}
                  </article>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
      <ConfirmModal
        open={deleteModalOpen}
        message="¿Desea eliminar la oferta?"
        confirmLabel="Eliminar"
        confirmDanger
        onConfirm={confirmDelete}
        onCancel={() => setDeleteModalOpen(false)}
      />
    </div>
  );
}
