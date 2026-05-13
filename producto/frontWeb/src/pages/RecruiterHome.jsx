import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import {
  getRecruiterCompanyProfile,
  getRecruiterCompanyProfileImageBlob,
  listMyJobs,
} from "../services/companyService";
import "../styles/Home.css";

function readUser() {
  try {
    return JSON.parse(localStorage.getItem("user") || "{}");
  } catch {
    return {};
  }
}

function recruiterFullName(user) {
  if (!user || typeof user !== "object") return "";
  const parts = [
    user.first_name,
    user.last_name_paternal,
    user.last_name_maternal,
  ].filter((x) => typeof x === "string" && x.trim() !== "");
  return parts.join(" ").trim();
}

export default function RecruiterHome() {
  const navigate = useNavigate();
  const user = readUser();
  const [jobsTotal, setJobsTotal] = useState(0);
  const [commercialName, setCommercialName] = useState("");
  const [corporateEmail, setCorporateEmail] = useState("");
  const [logoUrl, setLogoUrl] = useState(null);
  const blobUrlRef = useRef(null);

  useEffect(() => {
    let cancelled = false;

    const revokeBlob = () => {
      if (blobUrlRef.current) {
        URL.revokeObjectURL(blobUrlRef.current);
        blobUrlRef.current = null;
      }
    };

    (async () => {
      revokeBlob();
      setLogoUrl(null);
      setCommercialName("");
      setCorporateEmail("");

      const jobsPromise = listMyJobs(1, 20).catch(() => null);
      const profilePromise = getRecruiterCompanyProfile().catch(() => null);
      const [jobsData, profile] = await Promise.all([jobsPromise, profilePromise]);
      if (cancelled) return;

      if (jobsData) {
        setJobsTotal(jobsData.total_items ?? 0);
      } else {
        setJobsTotal(0);
      }

      if (profile) {
        setCommercialName(
          typeof profile.commercial_name === "string"
            ? profile.commercial_name
            : ""
        );
        setCorporateEmail(
          typeof profile.corporate_email === "string"
            ? profile.corporate_email
            : ""
        );
        if (profile.external_image_url) {
          setLogoUrl(profile.external_image_url);
        } else if (profile.has_protected_image) {
          try {
            const blob = await getRecruiterCompanyProfileImageBlob();
            if (cancelled) return;
            if (blob && blob.type && blob.type.startsWith("image/")) {
              const u = URL.createObjectURL(blob);
              blobUrlRef.current = u;
              setLogoUrl(u);
            }
          } catch {
            if (!cancelled) setLogoUrl(null);
          }
        }
      } else {
        setCommercialName("");
        setCorporateEmail("");
        setLogoUrl(null);
      }
    })();

    return () => {
      cancelled = true;
      revokeBlob();
    };
  }, []);

  return (
    <div>
      <Navbar />
      <div className="home-container">
        <div className="home-content">
          <div className="user-card" style={{ maxWidth: "100%" }}>
            {logoUrl ? (
              <img
                className="user-logo-img"
                src={logoUrl}
                alt=""
                width={96}
                height={96}
              />
            ) : (
              <div className="user-logo">Empresa</div>
            )}
            <div>
              <strong>{commercialName || "—"}</strong>
              {corporateEmail ? (
                <p style={{ margin: "4px 0 0", color: "#555", fontSize: "0.95rem" }}>
                  Correo corporativo:{" "}
                  <a href={`mailto:${corporateEmail}`}>{corporateEmail}</a>
                </p>
              ) : null}
              {recruiterFullName(user) ? (
                <p style={{ margin: "8px 0 0", color: "#333", fontWeight: 600 }}>
                  {recruiterFullName(user)}
                </p>
              ) : null}
              <p style={{ margin: "6px 0 0", color: "#555" }}>
                {user.email} — Reclutador
              </p>
            </div>
          </div>

          <div className="home-grid">
            <div className="recruitment-card">
              <div className="card-header">
                <h3>Reclutamiento</h3>
                <span onClick={() => navigate("/recruiter/reclutamiento")}>
                  Ir a módulo
                </span>
              </div>
              <div className="card-content">
                <p>
                  Ofertas publicadas
                  <span className="dots"></span>
                  <strong>{jobsTotal}</strong>
                </p>
              </div>
              <button
                type="button"
                className="primary-btn"
                onClick={() => navigate("/recruiter/ofertas")}
              >
                Ir a ver
              </button>
            </div>

            <div className="actions-container">
              <div className="action-card">
                <p>Postulaciones</p>
                <button
                  type="button"
                  className="secondary-btn"
                  onClick={() => navigate("/recruiter/postulaciones")}
                >
                  Ver postulaciones
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
