import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import ConfirmModal from "../components/ConfirmModal";
import ApplicationsCountChip from "../components/ApplicationsCountChip";
import {
  deleteJob,
  listMyJobs,
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

export default function RecruiterJobsList() {
  const navigate = useNavigate();
  const [items, setItems] = useState([]);
  const [error, setError] = useState("");
  const [statusFilter, setStatusFilter] = useState("ACTIVE");
  const [closingDueOn, setClosingDueOn] = useState(false);
  const [busyJobId, setBusyJobId] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const loadItems = useCallback(() => {
    setError("");
    const filter = closingDueOn ? "CLOSING_DUE" : statusFilter;
    return listMyJobs(1, 50, filter)
      .then((data) => setItems(data.items || []))
      .catch((err) => {
        setError(
          err.response?.data?.error?.message || "No se pudo cargar el listado"
        );
        setItems([]);
      });
  }, [statusFilter, closingDueOn]);

  useEffect(() => {
    loadItems();
  }, [loadItems]);

  const handleToggleStatus = async (job) => {
    if (!job?.id || busyJobId) return;
    const nextStatus = job.status === "INACTIVE" ? "ACTIVE" : "INACTIVE";
    setBusyJobId(job.id);
    try {
      await patchJobStatus(job.id, nextStatus);
      await loadItems();
    } catch (err) {
      setError(
        err.response?.data?.error?.message ||
          "No se pudo cambiar el estado de la oferta"
      );
    } finally {
      setBusyJobId(null);
    }
  };

  const confirmDelete = async () => {
    if (!deleteTarget?.id || busyJobId) return;
    setBusyJobId(deleteTarget.id);
    try {
      await deleteJob(deleteTarget.id);
      setDeleteTarget(null);
      await loadItems();
    } catch (err) {
      setError(
        err.response?.data?.error?.message || "No se pudo eliminar la oferta"
      );
    } finally {
      setBusyJobId(null);
    }
  };

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
              onClick={() => navigate("/recruiter/reclutamiento")}
            >
              Volver
            </button>
            <div className="recruiter-jobs-list-header">
              <h2 className="recruiter-jobs-list-title">Ofertas publicadas</h2>
              <div className="recruiter-jobs-list-filters">
                <button
                  type="button"
                  className={`recruiter-jobs-filter-btn${
                    !closingDueOn && statusFilter === "ACTIVE" ? " is-active" : ""
                  }`}
                  onClick={() => {
                    setClosingDueOn(false);
                    setStatusFilter("ACTIVE");
                  }}
                >
                  Activas
                </button>
                <button
                  type="button"
                  className={`recruiter-jobs-filter-btn${
                    !closingDueOn && statusFilter === "INACTIVE" ? " is-active" : ""
                  }`}
                  onClick={() => {
                    setClosingDueOn(false);
                    setStatusFilter("INACTIVE");
                  }}
                >
                  Inactivas
                </button>
                <button
                  type="button"
                  className={`recruiter-jobs-filter-btn${
                    closingDueOn ? " is-active" : ""
                  }`}
                  onClick={() => setClosingDueOn((on) => !on)}
                >
                  Por cerrar
                </button>
              </div>
            </div>
          </div>
          {error && <p style={{ color: "#b91c1c" }}>{error}</p>}
          <div className="recruitment-card" style={{ marginBottom: 12 }}>
            {items.length === 0 && !error && (
              <p style={{ color: "#64748b" }}>
                {closingDueOn
                  ? "No hay ofertas por cerrar."
                  : statusFilter === "ACTIVE"
                    ? "No hay ofertas activas."
                    : "No hay ofertas inactivas."}
              </p>
            )}
            {items.map((job) => {
              const isActive = job.status !== "INACTIVE";
              return (
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
                    <strong className="recruiter-jobs-list-item-title">
                      {job.title}
                    </strong>
                    <div style={{ fontSize: 13, color: "#64748b" }}>
                      {job.company_commercial_name} · ${job.salary_offered} ·{" "}
                    {MODALITY_LABELS[job.modality] ?? job.modality} ·{" "}
                    {WORKLOAD_LABELS[job.workload] ?? job.workload} · Fecha cierre:{" "}
                    {formatJobDateDisplay(job.closing_date)}
                  </div>
                  </div>
                  <div className="recruiter-jobs-list-actions">
                    <ApplicationsCountChip count={job.applications_count} />
                    <button
                      type="button"
                      className="secondary-btn"
                      onClick={() => navigate(`/recruiter/ofertas/${job.id}`)}
                    >
                      Ir a ver oferta
                    </button>
                    <button
                      type="button"
                      className={`recruiter-job-power-btn${
                        isActive
                          ? " recruiter-job-power-btn--active"
                          : " recruiter-job-power-btn--inactive"
                      }`}
                      onClick={() => handleToggleStatus(job)}
                      disabled={busyJobId === job.id}
                      aria-label={
                        isActive ? "Desactivar oferta" : "Activar oferta"
                      }
                    >
                      <PowerIcon />
                    </button>
                    <button
                      type="button"
                      className="recruiter-job-delete-btn"
                      onClick={() => setDeleteTarget(job)}
                      disabled={busyJobId === job.id}
                      aria-label="Eliminar oferta"
                    >
                      <DeleteIcon />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>
      <ConfirmModal
        open={deleteTarget !== null}
        message="¿Desea eliminar la oferta?"
        confirmLabel="Eliminar"
        confirmDanger
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}
