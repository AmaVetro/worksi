import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { listConversations } from "../services/messagingService";
import "../styles/Home.css";

export default function RecruiterReclutamiento() {
  const navigate = useNavigate();
  const [unreadChats, setUnreadChats] = useState(0);

  useEffect(() => {
    let cancelled = false;
    listConversations(1, 100)
      .then((data) => {
        if (cancelled) return;
        const rows = data.items || [];
        setUnreadChats(
          rows.filter((row) => (row.unread_count ?? 0) > 0).length
        );
      })
      .catch(() => {
        if (!cancelled) setUnreadChats(0);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div>
      <Navbar />
      <div className="home-container">
        <div className="home-content">
          <div className="actions-container recruiter-reclutamiento-actions">
            <div className="action-card recruiter-reclutamiento-card">
              <p className="recruiter-reclutamiento-card-title">
                Ofertas publicadas
              </p>
              <button
                type="button"
                className="secondary-btn"
                onClick={() => navigate("/recruiter/ofertas")}
              >
                Ir
              </button>
            </div>
            <div className="action-card recruiter-reclutamiento-card">
              <p className="recruiter-reclutamiento-card-title">
                Crear
                <br />
                oferta
              </p>
              <button
                type="button"
                className="secondary-btn"
                onClick={() => navigate("/recruiter/ofertas/nueva")}
              >
                Ir
              </button>
            </div>
            <div className="action-card recruiter-reclutamiento-card">
              <p className="recruiter-reclutamiento-card-title">
                <span className="recruiter-reclutamiento-card-title-text">
                  Matchs
                </span>
                {unreadChats > 0 && (
                  <span
                    className="recruiter-matchs-chats-badge"
                    aria-label={`${unreadChats} chats con mensajes sin leer`}
                  >
                    {unreadChats}
                  </span>
                )}
              </p>
              <button
                type="button"
                className="secondary-btn"
                onClick={() => navigate("/recruiter/matchs")}
              >
                Ir a ver
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
