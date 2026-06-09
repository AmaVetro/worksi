import "../styles/ConfirmModal.css";

export default function ConfirmModal({
  open,
  message,
  confirmLabel,
  onConfirm,
  onCancel,
  confirmDanger = false,
}) {
  if (!open) return null;

  return (
    <div
      className="confirm-modal-overlay"
      role="presentation"
      onClick={onCancel}
    >
      <div
        className="confirm-modal"
        role="dialog"
        aria-modal="true"
        onClick={(e) => e.stopPropagation()}
      >
        <p className="confirm-modal__message">{message}</p>
        <div className="confirm-modal__actions">
          <button
            type="button"
            className="confirm-modal__btn confirm-modal__btn--cancel"
            onClick={onCancel}
          >
            Cancelar
          </button>
          <button
            type="button"
            className={`confirm-modal__btn${
              confirmDanger ? " confirm-modal__btn--danger" : ""
            }`}
            onClick={onConfirm}
          >
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}
