import { useEffect, useState } from "react";
import { createConversation } from "../services/messagingService";
import "../styles/Home.css";

const FIRST_MESSAGE_MAX = 200;

export default function EstablishMatchModal({
  open,
  candidateName,
  applicationId,
  onClose,
  onGoToMessages,
}) {
  const [step, setStep] = useState("form");
  const [firstMessage, setFirstMessage] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const [conversationId, setConversationId] = useState(null);

  useEffect(() => {
    if (!open) return;
    setStep("form");
    setFirstMessage("");
    setError("");
    setBusy(false);
    setConversationId(null);
  }, [open, applicationId]);

  if (!open) return null;

  const trimmed = firstMessage.trim();
  const canSubmit = trimmed.length >= 1 && trimmed.length <= FIRST_MESSAGE_MAX;

  async function handleEstablish() {
    if (!canSubmit || !applicationId) return;
    setBusy(true);
    setError("");
    try {
      const res = await createConversation(Number(applicationId), trimmed);
      setConversationId(res.conversation_id);
      setStep("success");
    } catch (err) {
      setError(
        err.response?.data?.error?.message ||
          "No se pudo establecer el match"
      );
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="modal-overlay" role="presentation" onClick={onClose}>
      <div
        className="modal-card establish-match-modal"
        role="dialog"
        aria-modal="true"
        onClick={(e) => e.stopPropagation()}
      >
        {step === "form" && (
          <>
            <h3 className="establish-match-modal__title">
              Para establecer Match, envía un mensaje al candidato:
            </h3>
            <textarea
              className="establish-match-modal__textarea"
              value={firstMessage}
              maxLength={FIRST_MESSAGE_MAX}
              rows={4}
              onChange={(e) => setFirstMessage(e.target.value)}
              placeholder="Escriba su mensaje (máx. 200 caracteres)"
              disabled={busy}
            />
            <p className="establish-match-modal__hint">
              {trimmed.length}/{FIRST_MESSAGE_MAX}
            </p>
            {error && <p className="recruiter-job-detail-error">{error}</p>}
            <div className="establish-match-modal__actions">
              <button
                type="button"
                className="secondary-btn"
                onClick={onClose}
                disabled={busy}
              >
                Cancelar
              </button>
              <button
                type="button"
                className="primary-btn"
                onClick={handleEstablish}
                disabled={!canSubmit || busy}
              >
                Confirmar Match
              </button>
            </div>
          </>
        )}
        {step === "success" && (
          <>
            <h3 className="establish-match-modal__title">
              ¡Ha hecho match con candidato {candidateName}!
            </h3>
            <div className="establish-match-modal__actions establish-match-modal__actions--stack">
              <button
                type="button"
                className="primary-btn"
                onClick={() => {
                  onClose();
                  if (conversationId != null) {
                    onGoToMessages(conversationId);
                  }
                }}
              >
                Ir a mensajes
              </button>
              <button type="button" className="secondary-btn" onClick={onClose}>
                Volver
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
