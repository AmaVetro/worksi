import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { listCompanies, createRecruiter } from "../services/adminService";
import { passwordMatches } from "../utils/passwordPolicy";
import { isValidChileRut, normalizeRut } from "../utils/rutRules";
import { goBack } from "../utils/goBack";
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

export default function AdminRegisterRecruiter() {
  const navigate = useNavigate();
  const location = useLocation();
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
  const [companiesLoadError, setCompaniesLoadError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (location.pathname !== "/recruiters/nueva") return;
    let cancelled = false;
    listCompanies(1, 100)
      .then((data) => {
        if (cancelled) return;
        setCompanies(data.items || []);
        setCompaniesLoadError("");
      })
      .catch((err) => {
        if (cancelled) return;
        setCompanies([]);
        const status = err.response?.status;
        const msg = err.response?.data?.error?.message;
        const tail =
          status != null
            ? ` (HTTP ${status})`
            : err.message
              ? ` (${err.message})`
              : "";
        setCompaniesLoadError(
          msg
            ? `${msg}${tail}`
            : `No se pudo cargar el listado de empresas${tail}`
        );
      });
    return () => {
      cancelled = true;
    };
  }, [location.pathname]);

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
    req("password");
    req("mobile");
    req("birth_date");
    if (!form.company_id) {
      e.company_id = "Completa este campo";
      ok = false;
    }
    if (form.password && !passwordMatches(form.password)) {
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
        password: form.password,
        first_name: form.first_name.trim(),
        last_name_paternal: form.last_name_paternal.trim(),
        last_name_maternal: form.last_name_maternal.trim(),
        rut: normalizeRut(form.rut),
        mobile: form.mobile.trim(),
        birth_date: form.birth_date.trim(),
        company_id: Number(form.company_id),
      };
      if (form.phone.trim()) {
        payload.phone = form.phone.trim();
      }
      await createRecruiter(payload);
      window.alert("Registro de reclutador exitoso");
      navigate("/recruiters");
    } catch (err) {
      const msg =
        err.response?.data?.error?.message || "Error al registrar reclutador";
      setApiError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Navbar />
      <div className="admin-form-page">
        <div className="admin-form-inner">
          <h2>Registrar reclutadores</h2>
          
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
                Contraseña
                <input
                  type="password"
                  value={form.password}
                  onChange={(e) => setField("password", e.target.value)}
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
                {loading ? "Registrando…" : "Registrar"}
              </button>
              <button
                type="button"
                className="secondary-btn"
                onClick={() => goBack(navigate)}
              >
                Volver
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
