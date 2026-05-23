import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { getActiveJobsTotal } from "../services/adminService";
import "../styles/Home.css";

function Home() {
    const navigate = useNavigate();
    const [jobsTotal, setJobsTotal] = useState(null);

    useEffect(() => {
        let cancelled = false;
        (async () => {
            try {
                const total = await getActiveJobsTotal();
                if (!cancelled) {
                    setJobsTotal(total);
                }
            } catch {
                if (!cancelled) {
                    setJobsTotal(0);
                }
            }
        })();
        return () => {
            cancelled = true;
        };
    }, []);

    const jobsLabel =
        jobsTotal === null ? "…" : String(jobsTotal);

    return (
        <div>
        <Navbar />

        <div className="home-container">
            <div className="home-content">

            <div className="home-grid">

                <div className="recruitment-card">
                <div className="card-header">
                    <h3>Reclutamiento</h3>
                </div>

                <div className="card-content">
                    <p>
                    Ofertas Publicadas
                    <span className="dots"></span>
                    <strong>{jobsLabel}</strong>
                    </p>
                </div>

                <button type="button" className="primary-btn">Gestionar Ofertas</button>
                </div>

                <div className="actions-container">

                <div className="action-card">
                    <p>Registrar empresa</p>
                    <button type="button" className="secondary-btn" onClick={() => navigate("/companies")}>IR</button>
                </div>

                <div className="action-card">
                    <p>Registrar reclutador</p>
                    <button type="button" className="secondary-btn" onClick={() => navigate("/recruiters")}>IR</button>
                </div>

                </div>

            </div>

            </div>
        </div>
        </div>
    );
}

export default Home;
