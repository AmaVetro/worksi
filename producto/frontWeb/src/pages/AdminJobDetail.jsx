import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import ConfirmModal from "../components/ConfirmModal";
import ApplicationsCountChip from "../components/ApplicationsCountChip";
import {
  deleteJob,
  getJob,
  getJobImageBlob,
  patchJobStatus,
} from "../services/adminService";
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

export default function AdminJobDetail() {
  const { jobId } = useParams();
  const navigate = useNavigate();
  const [job, setJob] = useState(null);
  const [imageSrc, setImageSrc] = useState(null);
  const [error, setError] = useState("");
  const [lifecycleBusy, setLifecycleBusy] = useState(false);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const blobUrlRef = useRef(null);

  useEffect(() => {
    if (!jobId) return;
    let cancelled = false;
    const revoke = () => {
      if (blobUrlRef.current) {
        URL.revokeObjectURL(blobUrlRef.current);
        blobUrlRef.current = null;
      }
    };
    revoke();
    setJob(null);
    setImageSrc(null);
    setError("");
    getJob(jobId)
      .then(async (j) => {
        if (cancelled) return;
        let src = null;
        if (j.external_image_url) {
          src = j.external_image_url;
        } else if (j.has_protected_image) {
          try {
            const blob = await getJobImageBlob(jobId);
            if (cancelled) return;
            if (blob && blob.type && blob.type.startsWith("image/")) {
              const u = URL.createObjectURL(blob);
              blobUrlRef.current = u;
              src = u;
            }
          } catch {
            src = null;
          }
        }
        if (cancelled) return;
        setJob(j);
        setImageSrc(src);
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
      navigate("/ofertas");
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
            <div className="recruiter-job-detail-layout__body is-shown">
              <div className="recruiter-job-detail-top-row">
                <div className="recruiter-job-detail-top-back">
                  <button
                    type="button"
                    className="secondary-btn recruiter-job-detail-top-btn"
                    onClick={() => navigate("/ofertas")}
                  >
                    Volver al listado
                  </button>
                </div>
                {job && jobId && (
                  <button
                    type="button"
                    className="secondary-btn recruiter-job-detail-top-btn recruiter-job-detail-top-btn--edit"
                    data-shown="true"
                    onClick={() => navigate(`/ofertas/${jobId}/editar`)}
                  >
                    Editar oferta
                  </button>
                )}
              </div>
              {error && <p className="recruiter-job-detail-error">{error}</p>}
              {job && (
                <article className="recruitment-card recruiter-job-detail-card">
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
                  <div className="recruiter-job-detail-card-footer admin-job-detail-card-footer">
                    <div className="recruiter-job-detail-footer-actions">
                      <ApplicationsCountChip count={job.applications_count ?? 0} />
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
                    <span className="admin-job-recruiter-label">
                      Reclutador: {job.recruiter_name || "—"}
                    </span>
                    <div className="recruiter-job-detail-dates">
                      <p>
                        Fecha creación: {formatJobDateDisplay(job.created_at)}
                      </p>
                      <p>
                        Fecha cierre: {formatJobDateDisplay(job.closing_date)}
                      </p>
                    </div>
                  </div>
                </article>
              )}
              {!job && !error && <p>Cargando oferta…</p>}
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
