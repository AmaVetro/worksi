import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import { ViewApplicationsButton } from "../components/ApplicationsCountChip";
import ApplicationMatchRow, { candidateFullName } from "../components/ApplicationMatchRow";
import {
  getJob,
  getJobImageBlob,
  listJobApplications,
} from "../services/companyService";
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

export default function RecruiterJobDetail() {
  const { jobId } = useParams();
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const [job, setJob] = useState(null);
  const [error, setError] = useState("");
  const [imageSrc, setImageSrc] = useState(null);
  const [flipped, setFlipped] = useState(false);
  const [applications, setApplications] = useState([]);
  const [appsError, setAppsError] = useState("");
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
    if (!jobId) return;
    const showList = searchParams.has("postulaciones");
    setFlipped(showList);
    if (showList) {
      loadApplications();
    }
  }, [jobId, searchParams]);

  const loadApplications = () => {
    if (!jobId) return;
    setAppsError("");
    listJobApplications(jobId, 1, 50)
      .then((data) => setApplications(data.items || []))
      .catch((err) => {
        setApplications([]);
        setAppsError(
          err.response?.data?.error?.message ||
            "No se pudieron cargar las postulaciones"
        );
      });
  };

  const handleFlip = () => {
    if (flipped) {
      setSearchParams({}, { replace: true });
      setFlipped(false);
    } else {
      setSearchParams({ postulaciones: "1" }, { replace: true });
      loadApplications();
      setFlipped(true);
    }
  };

  const backFromApplicationsList = () => {
    setSearchParams({}, { replace: true });
    setFlipped(false);
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
      <div className="home-container">
        <div className="home-content recruiter-job-detail-shell">
          <div className="recruiter-job-detail-toolbar">
            <button
              type="button"
              className="secondary-btn recruiter-job-detail-toolbar-btn"
              onClick={() =>
                flipped ? backFromApplicationsList() : navigate("/recruiter/ofertas")
              }
            >
              {flipped ? "Volver al detalle" : "Volver al listado"}
            </button>
            {!error && jobId && !flipped && (
              <>
                <ViewApplicationsButton
                  count={appsCount}
                  onClick={handleFlip}
                />
                <button
                  type="button"
                  className="secondary-btn recruiter-job-detail-toolbar-btn"
                  onClick={() => navigate(`/recruiter/ofertas/${jobId}/editar`)}
                >
                  Editar oferta
                </button>
              </>
            )}
          </div>
          {error && <p className="recruiter-job-detail-error">{error}</p>}
          {job && (
            <div className={`job-flip-scene ${flipped ? "is-flipped" : ""}`}>
              <div className="job-flip-inner">
                <article className="recruitment-card recruiter-job-detail-card job-flip-front">
                  <header className="recruiter-job-detail-header">
                    <h2 className="recruiter-job-detail-title">{job.title}</h2>
                  </header>
                  {imageSrc ? (
                    <div className="recruiter-job-detail-image-wrap">
                      <img
                        className="recruiter-job-detail-image"
                        src={imageSrc}
                        alt=""
                      />
                    </div>
                  ) : null}
                  <p className="recruiter-job-detail-company">
                    {job.company_commercial_name}
                  </p>
                  <div className="recruiter-job-detail-body">
                    <p className="recruiter-job-detail-description">
                      {job.description}
                    </p>
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
                </article>

                <article className="recruitment-card recruiter-job-detail-card job-flip-back">
                  <h2 className="recruiter-job-detail-title">Postulaciones</h2>
                  {appsError && (
                    <p className="recruiter-job-detail-error">{appsError}</p>
                  )}
                  {applications.length === 0 && !appsError && (
                    <p style={{ color: "#64748b" }}>
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
                </article>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
