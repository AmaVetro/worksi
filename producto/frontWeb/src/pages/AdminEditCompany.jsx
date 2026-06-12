import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import { getCompany, updateCompany } from "../services/adminService";
import {
  fetchRegions,
  fetchCommunes,
  fetchSectors,
} from "../services/catalogService";
import { isValidChileRut, normalizeRut } from "../utils/rutRules";
import "../styles/AdminForms.css";

function emptyErrors() {
  return {
    rut: "",
    commercial_name: "",
    legal_name: "",
    phone: "",
    corporate_email: "",
    address: "",
    region_id: "",
    commune_id: "",
    sector_id: "",
    worker_count_approx: "",
  };
}

export default function AdminEditCompany() {
  const { companyId } = useParams();
  const navigate = useNavigate();
  const [regions, setRegions] = useState([]);
  const [communes, setCommunes] = useState([]);
  const [sectors, setSectors] = useState([]);
  const [regionId, setRegionId] = useState("");
  const [form, setForm] = useState({
    rut: "",
    commercial_name: "",
    legal_name: "",
    phone: "",
    corporate_email: "",
    address: "",
    commune_id: "",
    sector_id: "",
    worker_count_approx: "",
  });
  const [hasImage, setHasImage] = useState(false);
  const [image, setImage] = useState(null);
  const [fieldErrors, setFieldErrors] = useState(emptyErrors());
  const [apiError, setApiError] = useState("");
  const [loadError, setLoadError] = useState("");
  const [loading, setLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    Promise.all([fetchRegions(), fetchSectors(), getCompany(companyId)])
      .then(async ([regs, secs, detail]) => {
        if (cancelled) return;
        setRegions(regs);
        setSectors(secs);
        const rid = String(detail.region_id ?? "");
        setRegionId(rid);
        const comms = rid ? await fetchCommunes(rid) : [];
        if (cancelled) return;
        setCommunes(comms);
        setForm({
          rut: detail.rut ?? "",
          commercial_name: detail.commercial_name ?? "",
          legal_name: detail.legal_name ?? "",
          phone: detail.phone ?? "",
          corporate_email: detail.corporate_email ?? "",
          address: detail.address ?? "",
          commune_id: String(detail.commune_id ?? ""),
          sector_id: String(detail.sector_id ?? ""),
          worker_count_approx: String(detail.worker_count_approx ?? ""),
        });
        setHasImage(Boolean(detail.has_image));
        setLoadError("");
      })
      .catch((err) => {
        if (cancelled) return;
        setLoadError(
          err.response?.data?.error?.message ||
            "No se pudo cargar la empresa"
        );
      })
      .finally(() => {
        if (!cancelled) setPageLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [companyId]);

  const onRegionChange = (value) => {
    setRegionId(value);
    setForm((f) => ({ ...f, commune_id: "" }));
    setFieldErrors((er) => ({ ...er, region_id: "" }));
    setApiError("");
    if (!value) {
      setCommunes([]);
      return;
    }
    fetchCommunes(value)
      .then(setCommunes)
      .catch(() => setCommunes([]));
  };

  const setField = (key, value) => {
    setForm((f) => ({ ...f, [key]: value }));
    setFieldErrors((e) => ({ ...e, [key]: "" }));
    setApiError("");
  };

  const validate = () => {
    const e = emptyErrors();
    let ok = true;
    const check = (key) => {
      if (!String(form[key] ?? "").trim()) {
        e[key] = "Completa este campo";
        ok = false;
      }
    };
    check("rut");
    if (String(form.rut ?? "").trim() && !isValidChileRut(form.rut)) {
      e.rut = "RUT invalido (sin puntos, con guion y digito verificador)";
      ok = false;
    }
    check("commercial_name");
    check("legal_name");
    check("phone");
    check("corporate_email");
    const ce = String(form.corporate_email ?? "").trim();
    if (ce && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(ce)) {
      e.corporate_email = "Correo no válido";
      ok = false;
    }
    check("address");
    if (!regionId) {
      e.region_id = "Completa este campo";
      ok = false;
    }
    check("commune_id");
    check("sector_id");
    const w = String(form.worker_count_approx ?? "").trim();
    if (!w || Number(w) < 1) {
      e.worker_count_approx = "Completa este campo";
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
        commercial_name: form.commercial_name.trim(),
        legal_name: form.legal_name.trim(),
        phone: form.phone.trim(),
        corporate_email: form.corporate_email.trim(),
        address: form.address.trim(),
        rut: normalizeRut(form.rut),
        region_id: Number(regionId),
        commune_id: Number(form.commune_id),
        sector_id: Number(form.sector_id),
        worker_count_approx: Number(form.worker_count_approx),
      };
      await updateCompany(companyId, payload, image);
      window.alert("Empresa actualizada correctamente");
      navigate("/companies");
    } catch (err) {
      const msg =
        err.response?.data?.error?.message || "Error al actualizar empresa";
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
              onClick={() => navigate("/companies")}
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
            onClick={() => navigate("/companies")}
          >
            Volver
          </button>
          <h2>Editar empresa</h2>

          <form onSubmit={handleSubmit}>
            <div className="admin-form-grid">
              <label className="full">
                RUT (sin puntos, con guión)
                <input
                  value={form.rut}
                  onChange={(e) => setField("rut", e.target.value)}
                />
                {fieldErrors.rut && (
                  <span className="error-field">{fieldErrors.rut}</span>
                )}
              </label>
              <label>
                Nombre comercial
                <input
                  value={form.commercial_name}
                  onChange={(e) =>
                    setField("commercial_name", e.target.value)
                  }
                />
                {fieldErrors.commercial_name && (
                  <span className="error-field">
                    {fieldErrors.commercial_name}
                  </span>
                )}
              </label>
              <label>
                Razón social
                <input
                  value={form.legal_name}
                  onChange={(e) => setField("legal_name", e.target.value)}
                />
                {fieldErrors.legal_name && (
                  <span className="error-field">{fieldErrors.legal_name}</span>
                )}
              </label>
              <label>
                Teléfono de contacto
                <input
                  value={form.phone}
                  onChange={(e) => setField("phone", e.target.value)}
                />
                {fieldErrors.phone && (
                  <span className="error-field">{fieldErrors.phone}</span>
                )}
              </label>
              <label className="full">
                Correo corporativo
                <input
                  type="email"
                  autoComplete="email"
                  value={form.corporate_email}
                  onChange={(e) => setField("corporate_email", e.target.value)}
                />
                {fieldErrors.corporate_email && (
                  <span className="error-field">{fieldErrors.corporate_email}</span>
                )}
              </label>
              <label className="full">
                Dirección
                <input
                  value={form.address}
                  onChange={(e) => setField("address", e.target.value)}
                />
                {fieldErrors.address && (
                  <span className="error-field">{fieldErrors.address}</span>
                )}
              </label>
              <label>
                Región
                <select
                  value={regionId}
                  onChange={(e) => onRegionChange(e.target.value)}
                >
                  <option value="">Seleccione</option>
                  {regions.map((r) => (
                    <option key={r.id} value={r.id}>
                      {r.name}
                    </option>
                  ))}
                </select>
                {fieldErrors.region_id && (
                  <span className="error-field">{fieldErrors.region_id}</span>
                )}
              </label>
              <label>
                Comuna
                <select
                  value={form.commune_id}
                  onChange={(e) => setField("commune_id", e.target.value)}
                  disabled={!regionId}
                >
                  <option value="">Seleccione</option>
                  {communes.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.name}
                    </option>
                  ))}
                </select>
                {fieldErrors.commune_id && (
                  <span className="error-field">{fieldErrors.commune_id}</span>
                )}
              </label>
              <label>
                Sector o ámbito
                <select
                  value={form.sector_id}
                  onChange={(e) => setField("sector_id", e.target.value)}
                >
                  <option value="">Seleccione</option>
                  {sectors.map((s) => (
                    <option key={s.id} value={s.id}>
                      {s.name}
                    </option>
                  ))}
                </select>
                {fieldErrors.sector_id && (
                  <span className="error-field">{fieldErrors.sector_id}</span>
                )}
              </label>
              <label>
                N° trabajadores (aprox.)
                <input
                  type="number"
                  min={1}
                  value={form.worker_count_approx}
                  onChange={(e) =>
                    setField("worker_count_approx", e.target.value)
                  }
                />
                {fieldErrors.worker_count_approx && (
                  <span className="error-field">
                    {fieldErrors.worker_count_approx}
                  </span>
                )}
              </label>
              <label className="full">
                Imagen empresa (opcional, PNG o JPEG)
                {hasImage && (
                  <span className="hint"> Ya hay una imagen guardada.</span>
                )}
                <input
                  type="file"
                  accept="image/png,image/jpeg"
                  onChange={(e) =>
                    setImage(e.target.files && e.target.files[0]
                      ? e.target.files[0]
                      : null)
                  }
                />
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
                onClick={() => navigate("/companies")}
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
