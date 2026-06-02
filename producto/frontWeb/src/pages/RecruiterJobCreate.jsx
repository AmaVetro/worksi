import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { createJob, getRecruiterCompanyProfile } from "../services/companyService";
import {
  fetchRegions,
  fetchCommunes,
  fetchSectors,
  fetchSkillsBySector,
} from "../services/catalogService";
import { goBack } from "../utils/goBack";
import "../styles/AdminForms.css";

function emptyErrors() {
  return {
    company_commercial_name: "",
    title: "",
    description: "",
    region_id: "",
    commune_id: "",
    salary_offered: "",
    years_experience_required: "",
    modality: "",
    workload: "",
    skills: "",
    sector_skills: "",
    image_file: "",
  };
}

export default function RecruiterJobCreate() {
  const navigate = useNavigate();
  const [regions, setRegions] = useState([]);
  const [communes, setCommunes] = useState([]);
  const [sectors, setSectors] = useState([]);
  const [skills, setSkills] = useState([]);
  const [regionId, setRegionId] = useState("");
  const [sectorForSkills, setSectorForSkills] = useState("");
  const [selectedSkillIds, setSelectedSkillIds] = useState(new Set());
  const [form, setForm] = useState({
    company_commercial_name: "",
    title: "",
    description: "",
    commune_id: "",
    salary_offered: "",
    years_experience_required: "",
    modality: "REMOTE",
    workload: "FULL_TIME",
  });
  const [jobImageFile, setJobImageFile] = useState(null);
  const [fieldErrors, setFieldErrors] = useState(emptyErrors());
  const [apiError, setApiError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchRegions().then(setRegions).catch(() => setRegions([]));
    fetchSectors().then(setSectors).catch(() => setSectors([]));
    getRecruiterCompanyProfile()
      .then((profile) => {
        const name =
          typeof profile.commercial_name === "string"
            ? profile.commercial_name.trim()
            : "";
        if (name) {
          setForm((f) => ({ ...f, company_commercial_name: name }));
        }
      })
      .catch(() => {});
  }, []);

  const onJobRegionChange = (value) => {
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

  const onSectorForSkillsChange = (value) => {
    setSectorForSkills(value);
    setFieldErrors((er) => ({ ...er, sector_skills: "" }));
    setApiError("");
    if (!value) {
      setSkills([]);
      setSelectedSkillIds(new Set());
      return;
    }
    fetchSkillsBySector(value)
      .then((list) => {
        setSkills(list);
        setSelectedSkillIds(new Set());
      })
      .catch(() => {
        setSkills([]);
        setSelectedSkillIds(new Set());
      });
  };

  const setField = (key, value) => {
    setForm((f) => ({ ...f, [key]: value }));
    setFieldErrors((e) => ({ ...e, [key]: "" }));
    setApiError("");
  };

  const toggleSkill = (id) => {
    setSelectedSkillIds((prev) => {
      const next = new Set(prev);
      if (next.has(id)) {
        next.delete(id);
      } else if (next.size < 8) {
        next.add(id);
      }
      return next;
    });
    setFieldErrors((e) => ({ ...e, skills: "" }));
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
    req("company_commercial_name");
    req("title");
    req("description");
    if (!regionId) {
      e.region_id = "Completa este campo";
      ok = false;
    }
    req("commune_id");
    req("salary_offered");
    req("years_experience_required");
    req("modality");
    req("workload");
    if (!sectorForSkills) {
      e.sector_skills = "Seleccione rubro para skills";
      ok = false;
    }
    if (selectedSkillIds.size < 3 || selectedSkillIds.size > 8) {
      e.skills = "Elija entre 3 y 8 skills";
      ok = false;
    }
    if (jobImageFile) {
      const t = jobImageFile.type || "";
      if (t !== "image/png" && t !== "image/jpeg") {
        e.image_file = "Solo PNG o JPEG";
        ok = false;
      }
    }
    setFieldErrors(e);
    return ok;
  };

  const handleSubmit = async (ev) => {
    ev.preventDefault();
    setApiError("");
    if (!validate()) return;
    if (
      !window.confirm(
        "¿Confirmar publicación de esta oferta en el sistema?"
      )
    ) {
      return;
    }
    setLoading(true);
    try {
      const payload = {
        company_commercial_name: form.company_commercial_name.trim(),
        title: form.title.trim(),
        description: form.description.trim(),
        region_id: Number(regionId),
        commune_id: Number(form.commune_id),
        salary_offered: Number(form.salary_offered),
        years_experience_required: Number(form.years_experience_required),
        modality: form.modality,
        workload: form.workload,
        skills_ids: Array.from(selectedSkillIds),
      };
      await createJob(payload, jobImageFile);
      window.alert("Oferta publicada correctamente");
      navigate("/recruiter/ofertas");
    } catch (err) {
      const msg =
        err.response?.data?.error?.message || "Error al publicar oferta";
      setApiError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Navbar />
      <div className="admin-form-page">
        <div className="admin-form-inner" style={{ maxWidth: 800 }}>
          <button
            type="button"
            className="secondary-btn"
            style={{ marginTop: 0, marginBottom: 12 }}
            onClick={() => goBack(navigate)}
          >
            Volver
          </button>
          <h2>Crear oferta</h2>
          <form onSubmit={handleSubmit}>
            <div className="admin-form-grid">
              <label className="full">
                Nombre comercial de la empresa
                <input
                  value={form.company_commercial_name}
                  disabled
                />
                {fieldErrors.company_commercial_name && (
                  <span className="error-field">
                    {fieldErrors.company_commercial_name}
                  </span>
                )}
              </label>
              <label className="full">
                Título de la oferta
                <input
                  value={form.title}
                  onChange={(e) => setField("title", e.target.value)}
                />
                {fieldErrors.title && (
                  <span className="error-field">{fieldErrors.title}</span>
                )}
              </label>
              <label className="full">
                Descripción (único texto largo)
                <textarea
                  value={form.description}
                  onChange={(e) => setField("description", e.target.value)}
                />
                {fieldErrors.description && (
                  <span className="error-field">{fieldErrors.description}</span>
                )}
              </label>
              <label>
                Región (oferta)
                <select
                  value={regionId}
                  onChange={(e) => onJobRegionChange(e.target.value)}
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
                Comuna (oferta)
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
                Sueldo ofrecido
                <input
                  type="number"
                  min={1}
                  value={form.salary_offered}
                  onChange={(e) => setField("salary_offered", e.target.value)}
                />
                {fieldErrors.salary_offered && (
                  <span className="error-field">
                    {fieldErrors.salary_offered}
                  </span>
                )}
              </label>
              <label>
                Años de experiencia requeridos
                <input
                  type="number"
                  min={0}
                  max={80}
                  value={form.years_experience_required}
                  onChange={(e) =>
                    setField("years_experience_required", e.target.value)
                  }
                />
                {fieldErrors.years_experience_required && (
                  <span className="error-field">
                    {fieldErrors.years_experience_required}
                  </span>
                )}
              </label>
              <label>
                Modalidad
                <select
                  value={form.modality}
                  onChange={(e) => setField("modality", e.target.value)}
                >
                  <option value="REMOTE">Remoto</option>
                  <option value="HYBRID">Híbrido</option>
                  <option value="ONSITE">Presencial</option>
                </select>
              </label>
              <label>
                Carga horaria
                <select
                  value={form.workload}
                  onChange={(e) => setField("workload", e.target.value)}
                >
                  <option value="FULL_TIME">Full time</option>
                  <option value="PART_TIME">Part time</option>
                  <option value="OTHER">Otro</option>
                </select>
              </label>
              <label className="full">
                Rubro para elegir skills (catálogo)
                <select
                  value={sectorForSkills}
                  onChange={(e) => onSectorForSkillsChange(e.target.value)}
                >
                  <option value="">Seleccione</option>
                  {sectors.map((s) => (
                    <option key={s.id} value={s.id}>
                      {s.name}
                    </option>
                  ))}
                </select>
                {fieldErrors.sector_skills && (
                  <span className="error-field">{fieldErrors.sector_skills}</span>
                )}
              </label>
              <label className="full">
                Skills ({selectedSkillIds.size} seleccionadas, mín. 3 máx. 8)
                <div className="skills-box">
                  {skills.map((sk) => (
                    <label key={sk.id} className="row">
                      <input
                        type="checkbox"
                        checked={selectedSkillIds.has(sk.id)}
                        onChange={() => toggleSkill(sk.id)}
                      />
                      {sk.name}
                    </label>
                  ))}
                  {skills.length === 0 && sectorForSkills && (
                    <span style={{ color: "#64748b" }}>Sin skills en rubro.</span>
                  )}
                </div>
                {fieldErrors.skills && (
                  <span className="error-field">{fieldErrors.skills}</span>
                )}
              </label>
              <label className="full">
                Imagen de la oferta (opcional, PNG o JPEG)
                <input
                  type="file"
                  accept="image/png,image/jpeg"
                  onChange={(e) => {
                    const f = e.target.files && e.target.files[0];
                    setJobImageFile(f || null);
                    setFieldErrors((er) => ({ ...er, image_file: "" }));
                    setApiError("");
                  }}
                />
                {fieldErrors.image_file && (
                  <span className="error-field">{fieldErrors.image_file}</span>
                )}
              </label>
            </div>
            {apiError && <p className="api-error">{apiError}</p>}
            <div className="admin-form-actions">
              <button type="submit" className="primary-btn" disabled={loading}>
                {loading ? "Publicando…" : "Publicar"}
              </button>
              <button
                type="button"
                className="secondary-btn"
                onClick={() => navigate("/recruiter/ofertas")}
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
