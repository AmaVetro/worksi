import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { listCompanies } from "../services/adminService";
import "../styles/AdminForms.css";

export default function AdminCompaniesList() {
  const navigate = useNavigate();
  const [items, setItems] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

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
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
