import { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import {
  getConversation,
  listMessages,
  sendMessage,
} from "../services/messagingService";
import "../styles/Home.css";

const POLL_MS = 4000;
const MESSAGE_MAX = 500;

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

export default function RecruiterMessageThread() {
  const { conversationId } = useParams();
  const navigate = useNavigate();
  const [header, setHeader] = useState(null);
  const [messages, setMessages] = useState([]);
  const [draft, setDraft] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const lastIdRef = useRef(0);
  const listEndRef = useRef(null);

  const loadInitial = useCallback(async () => {
    const id = Number(conversationId);
    if (!id) return;
    setLoading(true);
    setError("");
    try {
      const [detail, page] = await Promise.all([
        getConversation(id),
        listMessages(id, { page: 1 }),
      ]);
      setHeader(detail);
      const items = page.items || [];
      setMessages(items);
      if (items.length > 0) {
        lastIdRef.current = Math.max(...items.map((m) => m.message_id));
      } else {
        lastIdRef.current = 0;
      }
    } catch (err) {
      setError(
        err.response?.data?.error?.message ||
          "No se pudo cargar la conversación"
      );
    } finally {
      setLoading(false);
    }
  }, [conversationId]);

  useEffect(() => {
    loadInitial();
  }, [loadInitial]);

  useEffect(() => {
    listEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  useEffect(() => {
    const id = Number(conversationId);
    if (!id || loading) return undefined;
    const timer = setInterval(async () => {
      try {
        const after = lastIdRef.current;
        if (after <= 0) return;
        const page = await listMessages(id, { afterMessageId: after });
        const incoming = page.items || [];
        if (incoming.length === 0) return;
        setMessages((prev) => {
          const ids = new Set(prev.map((m) => m.message_id));
          const merged = [...prev];
          for (const m of incoming) {
            if (!ids.has(m.message_id)) {
              merged.push(m);
            }
          }
          return merged.sort((a, b) => a.message_id - b.message_id);
        });
        lastIdRef.current = Math.max(
          lastIdRef.current,
          ...incoming.map((m) => m.message_id)
        );
      } catch {
        /* polling silencioso */
      }
    }, POLL_MS);
    return () => clearInterval(timer);
  }, [conversationId, loading]);

  async function handleSend(e) {
    e.preventDefault();
    const body = draft.trim();
    if (!body || body.length > MESSAGE_MAX) return;
    const id = Number(conversationId);
    setSending(true);
    setError("");
    try {
      const created = await sendMessage(id, body);
      setMessages((prev) => [...prev, created]);
      lastIdRef.current = Math.max(lastIdRef.current, created.message_id);
      setDraft("");
    } catch (err) {
      setError(
        err.response?.data?.error?.message || "No se pudo enviar el mensaje"
      );
    } finally {
      setSending(false);
    }
  }

  const trimmed = draft.trim();

  return (
    <div>
      <Navbar />
      <div className="home-container">
        <div className="home-content">
          <div className="recruitment-card recruiter-thread-card">
            <div className="recruiter-thread-top-row">
              <button
                type="button"
                className="secondary-btn recruiter-job-detail-top-btn"
                onClick={() => navigate("/recruiter/matchs")}
              >
                Volver
              </button>
            </div>
            {header && (
              <h2 className="recruiter-job-detail-title">
                Hilo con {header.candidate_display_name || "candidato"}
              </h2>
            )}
            {header?.job_title && (
              <p className="recruiter-candidate-profile-subtitle">
                {header.job_title}
              </p>
            )}
            {loading && <p>Cargando mensajes…</p>}
            {error && <p className="recruiter-job-detail-error">{error}</p>}
            <div className="recruiter-thread-messages">
              {messages.map((m) => (
                <div
                  key={m.message_id}
                  className={
                    m.sender_role === "RECRUITER"
                      ? "recruiter-thread-bubble recruiter-thread-bubble--mine"
                      : "recruiter-thread-bubble recruiter-thread-bubble--theirs"
                  }
                >
                  <p>{m.body}</p>
                  <span className="recruiter-thread-bubble__time">
                    {formatWhen(m.sent_at)}
                  </span>
                </div>
              ))}
              <div ref={listEndRef} />
            </div>
            <form className="recruiter-thread-compose" onSubmit={handleSend}>
              <textarea
                className="establish-match-modal__textarea"
                rows={3}
                maxLength={MESSAGE_MAX}
                value={draft}
                onChange={(e) => setDraft(e.target.value)}
                placeholder="Escriba un mensaje (máx. 500 caracteres)"
                disabled={sending || loading}
              />
              <button
                type="submit"
                className="primary-btn"
                disabled={
                  sending || loading || trimmed.length < 1 || trimmed.length > MESSAGE_MAX
                }
              >
                Enviar
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
