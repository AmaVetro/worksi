export function ApplicationsIcon({ className }) {
  return (
    <svg
      className={className}
      viewBox="0 0 36 36"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
    >
      <path
        d="M7 6h15a2.5 2.5 0 0 1 2.5 2.5v19a2.5 2.5 0 0 1-2.5 2.5H7A2.5 2.5 0 0 1 4.5 27.5v-19A2.5 2.5 0 0 1 7 6Z"
        stroke="currentColor"
        strokeWidth="1.75"
      />
      <path
        d="M9.5 12.5h10M9.5 17h10M9.5 21.5h6.5"
        stroke="currentColor"
        strokeWidth="1.75"
        strokeLinecap="round"
      />
      <circle
        cx="25"
        cy="23"
        r="8.5"
        fill="var(--applications-chip-bg, #e6f7f5)"
        stroke="currentColor"
        strokeWidth="1.75"
      />
      <circle cx="25" cy="21" r="2.4" fill="currentColor" />
      <path
        d="M20.8 26.2c.85-1.5 2.2-2.2 4.2-2.2s3.35.7 4.2 2.2"
        stroke="currentColor"
        strokeWidth="1.75"
        strokeLinecap="round"
      />
    </svg>
  );
}

export function ViewApplicationsButton({ count, onClick }) {
  const n = typeof count === "number" ? count : 0;

  return (
    <button
      type="button"
      className="applications-view-btn"
      onClick={onClick}
      title={`${n} postulación${n === 1 ? "" : "es"}`}
      aria-label={`Ver postulaciones, ${n} recibida${n === 1 ? "" : "s"}`}
    >
      <span className="applications-view-btn__label">Ver postulaciones</span>
      <span className="applications-count-chip__value">{n}</span>
      <ApplicationsIcon className="applications-count-chip__icon" />
    </button>
  );
}

export default function ApplicationsCountChip({ count }) {
  if (typeof count !== "number") {
    return null;
  }

  return (
    <div
      className="applications-count-chip"
      title={`${count} postulación${count === 1 ? "" : "es"} recibida${count === 1 ? "" : "s"}`}
      aria-label={`${count} postulaciones`}
    >
      <ApplicationsIcon className="applications-count-chip__icon" />
      <span className="applications-count-chip__value">{count}</span>
    </div>
  );
}
