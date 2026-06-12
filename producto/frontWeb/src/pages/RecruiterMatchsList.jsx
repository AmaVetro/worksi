import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import ApplicationMatchRow from "../components/ApplicationMatchRow";
import { listConversations } from "../services/messagingService";
import "../styles/Home.css";

function formatWhen(iso) {
  if (!iso) return "";
  try {
    return new Date(iso).toLocaleString("es-CL", {
      dateStyle: "short",
      timeStyle: "short",
    });
  } catch {
    return "";
  }
}

export default function RecruiterMatchsList() {
  const navigate = useNavigate();
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    listConversations(1, 50)
      .then((data) => {
        if (!cancelled) {
          setItems(data.items || []);
          setError("");
        }
      })
      .catch((err) => {
        if (!cancelled) {
          setItems([]);
          setError(
            err.response?.data?.error?.message ||
              "No se pudo cargar la bandeja de Matchs"
          );
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
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
          <div className="recruitment-card recruiter-matchs-card">
            <button
              type="button"
              className="secondary-btn"
              style={{ marginBottom: 16 }}
              onClick={() => navigate("/recruiter/reclutamiento")}
            >
              Volver
            </button>
            <h2 className="recruiter-job-detail-title">Matchs</h2>
            <p className="recruiter-matchs-subtitle">
              Conversa con tus candidatos favoritos.
            </p>
            {loading && <p>Cargando…</p>}
            {error && <p className="recruiter-job-detail-error">{error}</p>}
            {!loading && !error && items.length === 0 && (
              <div className="recruiter-matchs-empty">
                <p className="recruiter-matchs-empty__text">
                  Aún no hay conversaciones activas.
                </p>
              </div>
            )}
            <ul className="recruiter-matchs-list">
              {items.map((row) => {
                const unread = row.unread_count ?? 0;
                return (
                  <li key={row.conversation_id}>
                    <button
                      type="button"
                      className={
                        unread > 0
                          ? "recruiter-matchs-list__row recruiter-matchs-list__row--unread"
                          : "recruiter-matchs-list__row"
                      }
                      onClick={() =>
                        navigate(`/recruiter/matchs/${row.conversation_id}`)
                      }
                    >
                      <div className="recruiter-matchs-list__row-main">
                        <div className="recruiter-matchs-list__name-line">
                          <span className="recruiter-matchs-list__name">
                            {row.candidate_display_name || "Candidato"}
                          </span>
                          <div className="recruiter-matchs-list__match-wrap">
                            <ApplicationMatchRow score={row.match_score} />
                          </div>
                        </div>
                        <span className="recruiter-matchs-list__job">
                          {row.job_title}
                        </span>
                        <span className="recruiter-matchs-list__preview">
                          {row.last_message_preview}
                        </span>
                        <span className="recruiter-matchs-list__date">
                          {formatWhen(row.last_message_at)}
                        </span>
                      </div>
                      {unread > 0 && (
                        <span
                          className="recruiter-matchs-list__unread-badge"
                          aria-label={`${unread} mensajes sin leer`}
                        >
                          {unread}
                        </span>
                      )}
                    </button>
                  </li>
                );
              })}
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
