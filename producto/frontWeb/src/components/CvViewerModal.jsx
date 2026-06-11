import { useEffect, useRef } from "react";
import "../styles/CvViewerModal.css";

function parseFilename(contentDisposition, fallback) {
  if (!contentDisposition) return fallback;
  const match = /filename="([^"]+)"/i.exec(contentDisposition);
  if (match && match[1]) return match[1];
  return fallback;
}

export default function CvViewerModal({
  open,
  loading,
  error,
  pdfUrl,
  filename,
  onClose,
  onDownload,
}) {
  const overlayRef = useRef(null);

  useEffect(() => {
    if (!open) return undefined;
    const prev = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = prev;
    };
  }, [open]);

  if (!open) return null;

  return (
    <div
      className="cv-viewer-overlay"
      ref={overlayRef}
      role="presentation"
      onClick={onClose}
    >
      <div
        className="cv-viewer-modal"
        role="dialog"
        aria-modal="true"
        aria-label="Visualizador de CV"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          type="button"
          className="cv-viewer-close"
          onClick={onClose}
          aria-label="Cerrar"
        >
          ×
        </button>
        <div className="cv-viewer-body">
          {loading && <p className="cv-viewer-status">Cargando CV…</p>}
          {error && !loading && <p className="cv-viewer-error">{error}</p>}
          {!loading && !error && pdfUrl && (
            <iframe
              className="cv-viewer-frame"
              src={pdfUrl}
              title={filename || "CV del postulante"}
            />
          )}
        </div>
        <div className="cv-viewer-footer">
          <button
            type="button"
            className="primary-btn cv-viewer-download-btn"
            onClick={onDownload}
            disabled={loading || !!error || !pdfUrl}
          >
            Descargar CV
          </button>
        </div>
      </div>
    </div>
  );
}

export { parseFilename };
