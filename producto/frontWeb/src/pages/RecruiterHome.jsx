import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import {
  getRecruiterCompanyProfile,
  getRecruiterCompanyProfileImageBlob,
  getMyJobStats,
} from "../services/companyService";
import loginImage from "../assets/images/login-bg.jpg";
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
  const [jobsActive, setJobsActive] = useState(null);
  const [jobsInactive, setJobsInactive] = useState(null);
  const [jobsPublishedToday, setJobsPublishedToday] = useState(null);
  const [matchsTotal, setMatchsTotal] = useState(0);
  const [matchsToday, setMatchsToday] = useState(0);
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

      const jobsPromise = getMyJobStats().catch(() => null);
      const profilePromise = getRecruiterCompanyProfile().catch(() => null);
      const [jobStats, profile] = await Promise.all([jobsPromise, profilePromise]);
      if (cancelled) return;

      if (jobStats) {
        setJobsActive(jobStats.active_count ?? 0);
        setJobsInactive(jobStats.inactive_count ?? 0);
        setJobsPublishedToday(jobStats.published_today_count ?? 0);
      } else {
        setJobsActive(0);
        setJobsInactive(0);
        setJobsPublishedToday(0);
      }
      setMatchsTotal(0);
      setMatchsToday(0);

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
          <div className="recruiter-home-banner">
            <img src={loginImage} alt="" />
          </div>
          <div className="user-card recruiter-home-user-card">
            <div className="recruiter-home-profile">
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
            <div className="recruiter-home-visors">
              <button
                type="button"
                className="recruiter-home-visor recruiter-home-visor--jobs recruiter-home-visor--clickable"
                onClick={() => navigate("/recruiter/ofertas")}
              >
                <span className="recruiter-home-visor__label">Ofertas Totales</span>
                <div className="recruiter-home-visor__job-stats">
                  <div className="recruiter-home-visor__job-stat">
                    <span className="recruiter-home-visor__value">
                      {jobsActive === null ? "…" : jobsActive}
                    </span>
                    <span className="recruiter-home-visor__stat-label">Activas</span>
                  </div>
                  <div className="recruiter-home-visor__job-stat">
                    <span className="recruiter-home-visor__value recruiter-home-visor__value--inactive">
                      {jobsInactive === null ? "…" : jobsInactive}
                    </span>
                    <span className="recruiter-home-visor__stat-label">Inactivas</span>
                  </div>
                </div>
              </button>
              <div className="recruiter-home-visor">
                <span className="recruiter-home-visor__label">Publicadas Hoy</span>
                <span className="recruiter-home-visor__value">
                  {jobsPublishedToday === null ? "…" : jobsPublishedToday}
                </span>
              </div>
              <button
                type="button"
                className="recruiter-home-visor recruiter-home-visor--clickable"
              >
                <span className="recruiter-home-visor__label">Matchs Totales</span>
                <span className="recruiter-home-visor__value">{matchsTotal}</span>
              </button>
              <div className="recruiter-home-visor">
                <span className="recruiter-home-visor__label">Matchs Hoy</span>
                <span className="recruiter-home-visor__value">{matchsToday}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
