import Navbar from "../components/Navbar";
import "../styles/Home.css";

function Home() {
    return (
        <div>
        <Navbar />

        <div className="home-container">
            <div className="home-content">

            {/* 🔹 GRID PRINCIPAL */}
            <div className="home-grid">

                {/* 🔹 RECLUTAMIENTO */}
                <div className="recruitment-card">
                <div className="card-header">
                    <h3>Reclutamiento</h3>
                    <span>Gestionar avisos</span>
                </div>

                <div className="card-content">
                    <p>
                    Ofertas Publicadas
                    <span className="dots"></span>
                    <strong>16</strong>
                    </p>
                </div>

                <button className="primary-btn">Publicar aviso</button>
                </div>

                {/* 🔹 ACCIONES */}
                <div className="actions-container">

                <div className="action-card">
                    <p>Registrar empresa</p>
                    <button className="secondary-btn">IR</button>
                </div>

                <div className="action-card">
                    <p>Registrar reclutador</p>
                    <button className="secondary-btn">IR</button>
                </div>

                </div>

            </div>

            </div>
        </div>
        </div>
    );
}

export default Home;