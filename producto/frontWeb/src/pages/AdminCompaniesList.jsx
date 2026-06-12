import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import ConfirmModal from "../components/ConfirmModal";
import AdminListEditButton from "../components/AdminListEditButton";
import { deleteCompany, listCompanies } from "../services/adminService";
import "../styles/AdminForms.css";

export default function AdminCompaniesList() {
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
    return listCompanies(1, 100)
      .then((data) => setItems(data.items || []))
      .catch((err) => {
        setItems([]);
        setError(
          err.response?.data?.error?.message ||
            "No se pudo cargar el listado de empresas"
        );
      })
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const confirmDelete = async () => {
    if (!deleteTarget?.company_id || busyId) return;
    setBusyId(deleteTarget.company_id);
    setError("");
    setSuccess("");
    try {
      await deleteCompany(deleteTarget.company_id);
      setDeleteTarget(null);
      setSuccess("Empresa eliminada correctamente.");
      await load();
    } catch (err) {
      setError(
        err.response?.data?.error?.message || "No se pudo eliminar la empresa"
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
            <h2>Empresas registradas</h2>
            <button
              type="button"
              className="primary-btn admin-list-register-btn"
              onClick={() => navigate("/companies/nueva")}
            >
              Registrar empresa
            </button>
          </div>
          {loading && <p className="admin-list-status">Cargando…</p>}
          {error && <p className="admin-form-error">{error}</p>}
          {success && <p className="admin-list-success">{success}</p>}
          {!loading && !error && items.length === 0 && (
            <p className="admin-list-empty">
              No hay empresas registradas. Use «Registrar empresa» para dar de
              alta la primera.
            </p>
          )}
          {!loading && items.length > 0 && (
            <div className="admin-table-wrap">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>Nombre comercial</th>
                    <th>Correo corporativo</th>
                    <th>Teléfono</th>
                    <th>Región</th>
                    <th>Comuna</th>
                    <th>Sector</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {items.map((row) => (
                    <tr key={row.company_id}>
                      <td>{row.commercial_name}</td>
                      <td>{row.corporate_email}</td>
                      <td>{row.phone || "—"}</td>
                      <td>{row.region_name || "—"}</td>
                      <td>{row.commune_name || "—"}</td>
                      <td>{row.sector_name || "—"}</td>
                      <td>
                        <div className="admin-list-actions">
                          <AdminListEditButton
                            onClick={() =>
                              navigate(`/companies/${row.company_id}/editar`)
                            }
                          />
                          <button
                            type="button"
                            className="admin-list-delete-btn"
                            disabled={busyId === row.company_id}
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
        message={`¿Desea eliminar la empresa «${deleteTarget?.commercial_name ?? ""}»?`}
        confirmLabel="Eliminar"
        confirmDanger
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}
