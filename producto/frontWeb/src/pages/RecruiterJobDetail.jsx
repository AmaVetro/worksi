import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import { getJob } from "../services/companyService";
import "../styles/Home.css";

export default function RecruiterJobDetail() {
  const { jobId } = useParams();
  const navigate = useNavigate();
  const [job, setJob] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!jobId) return;
    getJob(jobId)
      .then(setJob)
      .catch((err) => {
        setError(
          err.response?.data?.error?.message || "No se pudo cargar la oferta"
        );
      });
  }, [jobId]);

  return (
    <div>
      <Navbar />
      <div className="home-container">
        <div className="home-content">
          <button
            type="button"
            className="secondary-btn"
            style={{ marginBottom: 16 }}
            onClick={() => navigate("/recruiter/ofertas")}
          >
            Volver al listado
          </button>
          {error && <p style={{ color: "#b91c1c" }}>{error}</p>}
          {job && (
            <div className="recruitment-card">
              <h2 style={{ marginTop: 0, color: "#0f766e" }}>{job.title}</h2>
              <p>
                <strong>{job.company_commercial_name}</strong>
              </p>
              <p>{job.description}</p>
              <p>
                Ciudad: {job.city} · Región id: {job.region_id} · Comuna id:{" "}
                {job.commune_id}
              </p>
              <p>
                Sueldo: ${job.salary_offered} · Años exp. requeridos:{" "}
                {job.years_experience_required}
              </p>
              <p>
                Modalidad: {job.modality} · Carga: {job.workload}
              </p>
              {job.image_url && <p>Imagen URL: {job.image_url}</p>}
              <p>Skills ids: {(job.skills_ids || []).join(", ")}</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
