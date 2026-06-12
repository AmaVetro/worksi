import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import ConfirmModal from "../components/ConfirmModal";
import AdminListEditButton from "../components/AdminListEditButton";
import { deleteRecruiter, listRecruiters } from "../services/adminService";
import "../styles/AdminForms.css";

function recruiterName(row) {
  const parts = [
    row.first_name,
    row.last_name_paternal,
    row.last_name_maternal,
  ].filter(Boolean);
  return parts.length > 0 ? parts.join(" ") : "—";
}

export default function AdminRecruitersList() {
  const navigate = useNavigate();
  const [items, setItems] = useState([]);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(true);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [busyId, setBusyId] = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    setError("");
    return listRecruiters(1, 100)
      .then((data) => setItems(data.items || []))
      .catch((err) => {
        setItems([]);
        setError(
          err.response?.data?.error?.message ||
            "No se pudo cargar el listado de reclutadores"
        );
      })
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const confirmDelete = async () => {
    if (!deleteTarget?.user_id || busyId) return;
    setBusyId(deleteTarget.user_id);
    setError("");
    setSuccess("");
    try {
      await deleteRecruiter(deleteTarget.user_id);
      setDeleteTarget(null);
      setSuccess("Reclutador eliminado correctamente.");
      await load();
    } catch (err) {
      setError(
        err.response?.data?.error?.message || "No se pudo eliminar el reclutador"
      );
    } finally {
      setBusyId(null);
    }
  };

  return (
    <div>
      <Navbar />
      <div className="admin-form-page">
        <div className="admin-form-inner admin-list-inner">
          <div className="admin-list-header">
            <h2>Reclutadores registrados</h2>
            <button
              type="button"
              className="primary-btn admin-list-register-btn"
              onClick={() => navigate("/recruiters/nueva")}
            >
              Registrar reclutador
            </button>
          </div>
          {loading && <p className="admin-list-status">Cargando…</p>}
          {error && <p className="admin-form-error">{error}</p>}
          {success && <p className="admin-list-success">{success}</p>}
          {!loading && !error && items.length === 0 && (
            <p className="admin-list-empty">
              No hay reclutadores registrados. Use «Registrar reclutador» para
              dar de alta el primero.
            </p>
          )}
          {!loading && items.length > 0 && (
            <div className="admin-table-wrap">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Email</th>
                    <th>Teléfono</th>
                    <th>Región</th>
                    <th>Comuna</th>
                    <th>Empresa</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {items.map((row) => (
                    <tr key={row.user_id}>
                      <td>{recruiterName(row)}</td>
                      <td>{row.email}</td>
                      <td>{row.phone || "—"}</td>
                      <td>{row.region_name || "—"}</td>
                      <td>{row.commune_name || "—"}</td>
                      <td>{row.company_commercial_name || "—"}</td>
                      <td>
                        <div className="admin-list-actions">
                          <AdminListEditButton
                            onClick={() =>
                              navigate(`/recruiters/${row.user_id}/editar`)
                            }
                          />
                          <button
                            type="button"
                            className="admin-list-delete-btn"
                            disabled={busyId === row.user_id}
                            onClick={() => setDeleteTarget(row)}
                          >
                            Eliminar
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
      <ConfirmModal
        open={deleteTarget !== null}
        message={`¿Desea eliminar al reclutador «${recruiterName(deleteTarget ?? {})}»?`}
        confirmLabel="Eliminar"
        confirmDanger
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}
