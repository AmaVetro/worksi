import { useNavigate, useLocation } from "react-router-dom";
import "../styles/Navbar.css";

function Navbar() {
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        localStorage.removeItem("token");
        navigate("/");
    };

    const isActive = (path) => location.pathname === path;

    return (
        <div className="navbar-container">

        {/* TOP BAR */}
        <div className="navbar-top">
            <h2 className="logo">
            Work<span>Sy</span>
            </h2>

            <div className="user-section">
            <span>Administrador</span>
            <button onClick={handleLogout}>Cerrar sesión</button>
            </div>
        </div>

        {/* MENU */}
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