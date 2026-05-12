import Navbar from "../components/Navbar";
import "../styles/Home.css";

export default function AdminSettingsPlaceholder() {
  return (
    <div>
      <Navbar />
      <div className="home-container">
        <p style={{ padding: 24 }}>Configuración: sin requisitos en el flujo actual.</p>
      </div>
    </div>
  );
}
