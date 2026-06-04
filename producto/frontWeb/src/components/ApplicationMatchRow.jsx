export function matchScorePercent(score) {
  if (score == null || Number.isNaN(Number(score))) return null;
  return Math.max(0, Math.min(100, Math.round(Number(score))));
}

export function matchLevelFromScore(score) {
  const percent = matchScorePercent(score);
  if (percent == null) return { text: "—", tone: "none" };
  if (percent >= 75) return { text: "Suficiente", tone: "high" };
  if (percent >= 25) return { text: "Intermedio", tone: "mid" };
  return { text: "Insuficiente", tone: "low" };
}

function matchScoreBarClass(percent) {
  if (percent == null) return "score-bar-fill--none";
  if (percent >= 75) return "score-bar-fill--high";
  if (percent >= 25) return "score-bar-fill--mid";
  return "score-bar-fill--low";
}

export default function ApplicationMatchRow({ score }) {
  const percent = matchScorePercent(score);
  const width = percent != null ? `${percent}%` : "0%";
  const label = percent != null ? `Match: ${percent}%` : "Match: —";

  return (
    <div className="applications-back-match-row">
      <span className="applications-back-match-label">{label}</span>
      <div
        className="score-bar-track applications-back-score-bar"
        role="progressbar"
        aria-valuemin={0}
        aria-valuemax={100}
        aria-valuenow={percent ?? 0}
        aria-label={
          percent != null ? `Compatibilidad ${percent}%` : "Compatibilidad no disponible"
        }
      >
        <div
          className={`score-bar-fill ${matchScoreBarClass(percent)}`}
          style={{ width }}
        />
      </div>
    </div>
  );
}

export function candidateFullName(preview) {
  if (!preview) return "Postulante";
  const parts = [
    preview.first_name,
    preview.middle_name,
    preview.last_name_paternal,
    preview.last_name_maternal,
  ].filter((p) => p && String(p).trim());
  return parts.length > 0 ? parts.join(" ") : "Postulante";
}
