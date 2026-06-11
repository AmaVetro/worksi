import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import ConfirmModal from "./ConfirmModal";
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
  const [logoutModalOpen, setLogoutModalOpen] = useState(false);

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
            <button type="button" onClick={() => setLogoutModalOpen(true)}>
              Cerrar sesión
            </button>
          </div>
        </div>
        <ConfirmModal
          open={logoutModalOpen}
          message="¿Desea cerrar sesión?"
          confirmLabel="Cerrar Sesión"
          onConfirm={() => {
            setLogoutModalOpen(false);
            handleLogout();
          }}
          onCancel={() => setLogoutModalOpen(false)}
        />
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
              location.pathname.startsWith("/recruiter/ofertas")
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
          <button type="button" onClick={() => setLogoutModalOpen(true)}>
            Cerrar sesión
          </button>
        </div>
      </div>
      <ConfirmModal
        open={logoutModalOpen}
        message="¿Desea cerrar sesión?"
        confirmLabel="Cerrar Sesión"
        onConfirm={() => {
          setLogoutModalOpen(false);
          handleLogout();
        }}
        onCancel={() => setLogoutModalOpen(false)}
      />
      <div className="navbar-menu">
        <div
          className={`nav-item ${isActive("/home") ? "active" : ""}`}
          onClick={() => navigate("/home")}
        >
          Inicio
        </div>
        <div
          className={`nav-item ${
            location.pathname === "/companies" ||
            location.pathname.startsWith("/companies/")
              ? "active"
              : ""
          }`}
          onClick={() => navigate("/companies")}
        >
          Empresas
        </div>
        <div
          className={`nav-item ${
            location.pathname === "/recruiters" ||
            location.pathname.startsWith("/recruiters/")
              ? "active"
              : ""
          }`}
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
