import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import CvViewerModal, { parseFilename } from "../components/CvViewerModal";
import EstablishMatchModal from "../components/EstablishMatchModal";
import {
  getApplicationCvFile,
  getCandidateProfileForApplication,
  getJob,
} from "../services/companyService";
import "../styles/Home.css";
import "../styles/CvViewerModal.css";

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

function formatSalary(min, max) {
  if (min == null && max == null) return "No indicado";
  if (min != null && max != null) {
    return `$${Number(min).toLocaleString("es-CL")} – $${Number(max).toLocaleString("es-CL")}`;
  }
  const v = min ?? max;
  return v != null ? `$${Number(v).toLocaleString("es-CL")}` : "No indicado";
}

function profileFullName(p) {
  if (!p) return "Postulante";
  const parts = [
    p.first_name,
    p.middle_name,
    p.last_name_paternal,
    p.last_name_maternal,
  ].filter(Boolean);
  return parts.length > 0 ? parts.join(" ") : "Postulante";
}

export default function RecruiterCandidateProfile() {
  const { jobId, applicationId } = useParams();
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [jobTitle, setJobTitle] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [cvModalOpen, setCvModalOpen] = useState(false);
  const [cvLoading, setCvLoading] = useState(false);
  const [cvError, setCvError] = useState("");
  const [cvUrl, setCvUrl] = useState("");
  const [cvFilename, setCvFilename] = useState("cv.pdf");
  const [matchModalOpen, setMatchModalOpen] = useState(false);
  const cvBlobRef = useRef(null);

  useEffect(() => {
    if (!jobId || !applicationId) return;
    let cancelled = false;
    setLoading(true);
    Promise.all([
      getCandidateProfileForApplication(jobId, applicationId),
      getJob(jobId),
    ])
      .then(([prof, job]) => {
        if (cancelled) return;
        setProfile(prof);
        setJobTitle(job?.title || "");
        setError("");
      })
      .catch((err) => {
        if (!cancelled) {
          setProfile(null);
          setError(
            err.response?.data?.error?.message ||
              "No se pudo cargar el perfil del postulante"
          );
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [jobId, applicationId]);

  useEffect(() => {
    return () => {
      if (cvUrl) {
        URL.revokeObjectURL(cvUrl);
      }
    };
  }, [cvUrl]);

  function closeCvModal() {
    setCvModalOpen(false);
    setCvError("");
    if (cvUrl) {
      URL.revokeObjectURL(cvUrl);
      setCvUrl("");
    }
    cvBlobRef.current = null;
  }

  function openCvModal() {
    if (!jobId || !applicationId) return;
    setCvModalOpen(true);
    setCvLoading(true);
    setCvError("");
    if (cvUrl) {
      URL.revokeObjectURL(cvUrl);
      setCvUrl("");
    }
    cvBlobRef.current = null;
    getApplicationCvFile(jobId, applicationId, false)
      .then((response) => {
        const blob = response.data;
        if (!blob || blob.size === 0) {
          setCvError("No hay CV disponible para este postulante");
          return;
        }
        const name = parseFilename(
          response.headers["content-disposition"],
          "cv.pdf"
        );
        setCvFilename(name);
        cvBlobRef.current = blob;
        setCvUrl(URL.createObjectURL(blob));
      })
      .catch((err) => {
        setCvError(
          err.response?.data?.error?.message ||
            "No se pudo cargar el CV del postulante"
        );
      })
      .finally(() => setCvLoading(false));
  }

  function downloadCv() {
    if (!cvBlobRef.current) return;
    const link = document.createElement("a");
    link.href = URL.createObjectURL(cvBlobRef.current);
    link.download = cvFilename || "cv.pdf";
    document.body.appendChild(link);
    link.click();
    link.remove();
    URL.revokeObjectURL(link.href);
  }

  const name = profileFullName(profile);
  const location =
    profile?.commune_name && profile?.region_name
      ? `${profile.commune_name} – ${profile.region_name}`
      : profile?.region_name || "—";

  return (
    <div>
      <Navbar />
      <div className="home-container home-container--job-detail">
        <div className="home-content recruiter-job-detail-shell">
          <div className="recruiter-job-detail-layout">
            <div className="recruiter-job-detail-layout__body">
              <div className="recruiter-job-detail-top-row">
                <button
                  type="button"
                  className="secondary-btn recruiter-job-detail-top-btn"
                  onClick={() =>
                    navigate(
                      `/recruiter/ofertas/${jobId}/postulaciones/${applicationId}`
                    )
                  }
                >
                  Volver
                </button>
              </div>
              {loading && (
                <p className="recruiter-job-detail-error">Cargando perfil…</p>
              )}
              {error && <p className="recruiter-job-detail-error">{error}</p>}
              {profile && (
                <article className="recruitment-card recruiter-candidate-profile-card">
                  <h2 className="recruiter-job-detail-title">
                    Perfil postulante
                  </h2>
                  {jobTitle ? (
                    <p className="recruiter-candidate-profile-subtitle">
                      Postulación para {jobTitle}
                    </p>
                  ) : null}
                  <div className="recruiter-candidate-profile-header">
                    <div>
                      <p className="recruiter-candidate-profile-name">
                        <strong>{name}</strong>
                      </p>
                      <p className="recruiter-candidate-profile-meta">
                        Rubro: {profile.sector_name || "—"}
                      </p>
                      <p className="recruiter-candidate-profile-meta">
                        {location}
                      </p>
                      <p className="recruiter-candidate-profile-contact">
                        {profile.email}
                        {profile.phone ? ` · ${profile.phone}` : ""}
                      </p>
                    </div>
                    <button
                      type="button"
                      className="recruiter-match-btn recruiter-establecer-match-btn"
                      onClick={() => setMatchModalOpen(true)}
                    >
                      <span>Establecer Match</span>
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
                  </div>
                  <section className="recruiter-candidate-profile-section">
                    <h3>Descripción</h3>
                    <p>
                      {profile.profile_summary?.trim()
                        ? profile.profile_summary
                        : "Sin descripción personal."}
                    </p>
                    <button
                      type="button"
                      className="primary-btn recruiter-candidate-profile-cv-btn"
                      onClick={openCvModal}
                    >
                      Ver CV
                    </button>
                  </section>
                  <section className="recruiter-candidate-profile-section">
                    <h3>Sueldo esperado</h3>
                    <p>
                      {formatSalary(
                        profile.salary_expected_min,
                        profile.salary_expected_max
                      )}
                    </p>
                  </section>
                  <section className="recruiter-candidate-profile-section">
                    <h3>Años de experiencia</h3>
                    <p>{profile.years_experience ?? 0} años</p>
                  </section>
                  <section className="recruiter-candidate-profile-section">
                    <h3>Modalidades preferidas</h3>
                    <div className="recruiter-candidate-profile-chips">
                      {(profile.preferred_modalities || []).length > 0 ? (
                        profile.preferred_modalities.map((m) => (
                          <span key={m} className="recruiter-candidate-chip">
                            {MODALITY_LABELS[m] || m}
                          </span>
                        ))
                      ) : (
                        <span className="recruiter-candidate-profile-muted">
                          —
                        </span>
                      )}
                    </div>
                  </section>
                  <section className="recruiter-candidate-profile-section">
                    <h3>Cargas horarias preferidas</h3>
                    <div className="recruiter-candidate-profile-chips">
                      {(profile.preferred_workloads || []).length > 0 ? (
                        profile.preferred_workloads.map((w) => (
                          <span key={w} className="recruiter-candidate-chip">
                            {WORKLOAD_LABELS[w] || w}
                          </span>
                        ))
                      ) : (
                        <span className="recruiter-candidate-profile-muted">
                          —
                        </span>
                      )}
                    </div>
                  </section>
                  <section className="recruiter-candidate-profile-section">
                    <h3>Skills</h3>
                    <div className="recruiter-candidate-profile-chips">
                      {(profile.skills || []).length > 0 ? (
                        profile.skills.map((s) => (
                          <span
                            key={s.id}
                            className="recruiter-candidate-chip recruiter-candidate-chip--skill"
                          >
                            {s.name}
                          </span>
                        ))
                      ) : (
                        <span className="recruiter-candidate-profile-muted">
                          —
                        </span>
                      )}
                    </div>
                  </section>
                </article>
              )}
            </div>
          </div>
        </div>
      </div>
      <CvViewerModal
        open={cvModalOpen}
        loading={cvLoading}
        error={cvError}
        pdfUrl={cvUrl}
        filename={cvFilename}
        onClose={closeCvModal}
        onDownload={downloadCv}
      />
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
