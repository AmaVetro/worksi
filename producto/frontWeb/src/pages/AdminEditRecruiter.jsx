import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import {
  getRecruiter,
  listCompanies,
  updateRecruiter,
} from "../services/adminService";
import { passwordMatches } from "../utils/passwordPolicy";
import { isValidChileRut, normalizeRut } from "../utils/rutRules";
import "../styles/AdminForms.css";

function emptyErrors() {
  return {
    first_name: "",
    last_name_paternal: "",
    last_name_maternal: "",
    rut: "",
    email: "",
    password: "",
    mobile: "",
    company_id: "",
    birth_date: "",
  };
}

export default function AdminEditRecruiter() {
  const { userId } = useParams();
  const navigate = useNavigate();
  const [companies, setCompanies] = useState([]);
  const [form, setForm] = useState({
    first_name: "",
    last_name_paternal: "",
    last_name_maternal: "",
    rut: "",
    email: "",
    password: "",
    phone: "",
    mobile: "",
    company_id: "",
    birth_date: "",
  });
  const [fieldErrors, setFieldErrors] = useState(emptyErrors());
  const [apiError, setApiError] = useState("");
  const [loadError, setLoadError] = useState("");
  const [companiesLoadError, setCompaniesLoadError] = useState("");
  const [loading, setLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    Promise.all([listCompanies(1, 100), getRecruiter(userId)])
      .then(([companiesData, detail]) => {
        if (cancelled) return;
        setCompanies(companiesData.items || []);
        setCompaniesLoadError("");
        setForm({
          first_name: detail.first_name ?? "",
          last_name_paternal: detail.last_name_paternal ?? "",
          last_name_maternal: detail.last_name_maternal ?? "",
          rut: detail.rut ?? "",
          email: detail.email ?? "",
          password: "",
          phone: detail.phone ?? "",
          mobile: detail.mobile ?? "",
          company_id: String(detail.company_id ?? ""),
          birth_date: detail.birth_date ?? "",
        });
        setLoadError("");
      })
      .catch((err) => {
        if (cancelled) return;
        setLoadError(
          err.response?.data?.error?.message ||
            "No se pudo cargar el reclutador"
        );
      })
      .finally(() => {
        if (!cancelled) setPageLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [userId]);

  const setField = (key, value) => {
    setForm((f) => ({ ...f, [key]: value }));
    setFieldErrors((e) => ({ ...e, [key]: "" }));
    setApiError("");
  };

  const validate = () => {
    const e = emptyErrors();
    let ok = true;
    const req = (key) => {
      if (!String(form[key] ?? "").trim()) {
        e[key] = "Completa este campo";
        ok = false;
      }
    };
    req("first_name");
    req("last_name_paternal");
    req("last_name_maternal");
    req("rut");
    if (String(form.rut ?? "").trim() && !isValidChileRut(form.rut)) {
      e.rut = "RUT invalido (sin puntos, con guion y digito verificador)";
      ok = false;
    }
    req("email");
    req("mobile");
    req("birth_date");
    if (!form.company_id) {
      e.company_id = "Completa este campo";
      ok = false;
    }
    if (form.password.trim() && !passwordMatches(form.password)) {
      e.password =
        "Mínimo 10 caracteres: mayúscula, minúscula, número y símbolo";
      ok = false;
    }
    setFieldErrors(e);
    return ok;
  };

  const handleSubmit = async (ev) => {
    ev.preventDefault();
    setApiError("");
    if (!validate()) return;
    setLoading(true);
    try {
      const payload = {
        email: form.email.trim(),
        first_name: form.first_name.trim(),
        last_name_paternal: form.last_name_paternal.trim(),
        last_name_maternal: form.last_name_maternal.trim(),
        rut: normalizeRut(form.rut),
        mobile: form.mobile.trim(),
        birth_date: form.birth_date.trim(),
        company_id: Number(form.company_id),
      };
      if (form.password.trim()) {
        payload.password = form.password;
      }
      if (form.phone.trim()) {
        payload.phone = form.phone.trim();
      }
      await updateRecruiter(userId, payload);
      window.alert("Reclutador actualizado correctamente");
      navigate("/recruiters");
    } catch (err) {
      const msg =
        err.response?.data?.error?.message || "Error al actualizar reclutador";
      setApiError(msg);
    } finally {
      setLoading(false);
    }
  };

  if (pageLoading) {
    return (
      <div>
        <Navbar />
        <div className="admin-form-page">
          <div className="admin-form-inner">
            <p className="admin-list-status">Cargando…</p>
          </div>
        </div>
      </div>
    );
  }

  if (loadError) {
    return (
      <div>
        <Navbar />
        <div className="admin-form-page">
          <div className="admin-form-inner">
            <button
              type="button"
              className="secondary-btn admin-edit-back-btn"
              onClick={() => navigate("/recruiters")}
            >
              Volver
            </button>
            <p className="api-error">{loadError}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div>
      <Navbar />
      <div className="admin-form-page">
        <div className="admin-form-inner">
          <button
            type="button"
            className="secondary-btn admin-edit-back-btn"
            onClick={() => navigate("/recruiters")}
          >
            Volver
          </button>
          <h2>Editar reclutador</h2>

          {companiesLoadError && (
            <p className="api-error">{companiesLoadError}</p>
          )}
          <form onSubmit={handleSubmit}>
            <div className="admin-form-grid">
              <label>
                Nombre
                <input
                  value={form.first_name}
                  onChange={(e) => setField("first_name", e.target.value)}
                />
                {fieldErrors.first_name && (
                  <span className="error-field">{fieldErrors.first_name}</span>
                )}
              </label>
              <label>
                Apellido paterno
                <input
                  value={form.last_name_paternal}
                  onChange={(e) =>
                    setField("last_name_paternal", e.target.value)
                  }
                />
                {fieldErrors.last_name_paternal && (
                  <span className="error-field">
                    {fieldErrors.last_name_paternal}
                  </span>
                )}
              </label>
              <label>
                Apellido materno
                <input
                  value={form.last_name_maternal}
                  onChange={(e) =>
                    setField("last_name_maternal", e.target.value)
                  }
                />
                {fieldErrors.last_name_maternal && (
                  <span className="error-field">
                    {fieldErrors.last_name_maternal}
                  </span>
                )}
              </label>
              <label>
                RUT (sin puntos, con guión)
                <input
                  value={form.rut}
                  onChange={(e) => setField("rut", e.target.value)}
                  placeholder="12345678-9"
                />
                {fieldErrors.rut && (
                  <span className="error-field">{fieldErrors.rut}</span>
                )}
              </label>
              <label>
                Correo de acceso
                <input
                  type="email"
                  value={form.email}
                  onChange={(e) => setField("email", e.target.value)}
                />
                {fieldErrors.email && (
                  <span className="error-field">{fieldErrors.email}</span>
                )}
              </label>
              <label>
                Nueva contraseña (opcional)
                <input
                  type="password"
                  value={form.password}
                  onChange={(e) => setField("password", e.target.value)}
                  placeholder="Dejar vacío para no cambiar"
                />
                {fieldErrors.password && (
                  <span className="error-field">{fieldErrors.password}</span>
                )}
              </label>
              <label>
                Celular
                <input
                  value={form.mobile}
                  onChange={(e) => setField("mobile", e.target.value)}
                />
                {fieldErrors.mobile && (
                  <span className="error-field">{fieldErrors.mobile}</span>
                )}
              </label>
              <label>
                Teléfono (opcional)
                <input
                  value={form.phone}
                  onChange={(e) => setField("phone", e.target.value)}
                />
              </label>
              <label>
                Fecha de nacimiento
                <input
                  type="date"
                  value={form.birth_date}
                  onChange={(e) => setField("birth_date", e.target.value)}
                />
                {fieldErrors.birth_date && (
                  <span className="error-field">{fieldErrors.birth_date}</span>
                )}
              </label>
              <label className="full">
                Empresa
                <select
                  value={form.company_id}
                  onChange={(e) => setField("company_id", e.target.value)}
                >
                  <option value="">Seleccione empresa</option>
                  {companies.map((c) => (
                    <option key={c.company_id} value={c.company_id}>
                      {c.commercial_name} — {c.corporate_email || "—"} — {c.rut}
                    </option>
                  ))}
                </select>
                {fieldErrors.company_id && (
                  <span className="error-field">{fieldErrors.company_id}</span>
                )}
              </label>
            </div>
            {apiError && <p className="api-error">{apiError}</p>}
            <div className="admin-form-actions">
              <button type="submit" className="primary-btn" disabled={loading}>
                {loading ? "Guardando…" : "Guardar"}
              </button>
              <button
                type="button"
                className="secondary-btn"
                onClick={() => navigate("/recruiters")}
              >
                Cancelar
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
