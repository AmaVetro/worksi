import { useNavigate, useLocation } from "react-router-dom";
import "../styles/Navbar.css";

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

function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const user = readUser();
  const role = user.role;
  const isRecruiterShell = location.pathname.startsWith("/recruiter");

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/");
  };

  const isActive = (path) => location.pathname === path;

  const roleLabel =
    role === "ADMIN"
      ? "Administrador"
      : role === "RECRUITER"
        ? "Reclutador"
        : role || "Usuario";

  if (isRecruiterShell && role === "RECRUITER") {
    return (
      <div className="navbar-container">
        <div className="navbar-top">
          <h2 className="logo">
            Work<span>Sí</span>
          </h2>
          <div className="user-section">
            <span>
              {roleLabel} · {recruiterFullName(user) || user.email || ""}
            </span>
            <button type="button" onClick={handleLogout}>
              Cerrar sesión
            </button>
          </div>
        </div>
        <div className="navbar-menu">
          <div
            className={`nav-item ${isActive("/recruiter/home") ? "active" : ""}`}
            onClick={() => navigate("/recruiter/home")}
          >
            Inicio
          </div>
          <div
            className={`nav-item ${
              location.pathname.startsWith("/recruiter/reclutamiento") ||
              location.pathname.startsWith("/recruiter/ofertas") ||
              location.pathname.startsWith("/recruiter/postulaciones")
                ? "active"
                : ""
            }`}
            onClick={() => navigate("/recruiter/reclutamiento")}
          >
            Reclutamiento
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="navbar-container">
      <div className="navbar-top">
        <h2 className="logo">
          Work<span>Sí</span>
        </h2>
        <div className="user-section">
          <span>
            {roleLabel}
            {user.email ? ` · ${user.email}` : ""}
          </span>
          <button type="button" onClick={handleLogout}>
            Cerrar sesión
          </button>
        </div>
      </div>
      <div className="navbar-menu">
        <div
          className={`nav-item ${isActive("/home") ? "active" : ""}`}
          onClick={() => navigate("/home")}
        >
          Inicio
        </div>
        <div
          className={`nav-item ${isActive("/companies") ? "active" : ""}`}
          onClick={() => navigate("/companies")}
        >
          Empresas
        </div>
        <div
          className={`nav-item ${isActive("/recruiters") ? "active" : ""}`}
          onClick={() => navigate("/recruiters")}
        >
          Reclutadores
        </div>
        <div
          className={`nav-item ${isActive("/settings") ? "active" : ""}`}
          onClick={() => navigate("/settings")}
        >
          Configuración
        </div>
      </div>
    </div>
  );
}

export default Navbar;
