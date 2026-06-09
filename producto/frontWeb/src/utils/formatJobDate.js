export function todayDateInputValue() {
  const d = new Date();
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}

export function todayDateDisplayValue() {
  return formatJobDateDisplay(todayDateInputValue());
}

export function toDateInputValue(value) {
  if (!value) return "";
  if (typeof value === "string") {
    const datePart = value.length >= 10 ? value.slice(0, 10) : value;
    if (/^\d{4}-\d{2}-\d{2}$/.test(datePart)) {
      return datePart;
    }
  }
  return "";
}

export function formatJobDateDisplay(value) {
  if (!value) return "—";
  if (typeof value === "string") {
    const datePart = value.length >= 10 ? value.slice(0, 10) : value;
    const isoMatch = datePart.match(/^(\d{4})-(\d{2})-(\d{2})$/);
    if (isoMatch) {
      return `${isoMatch[3]}/${isoMatch[2]}/${isoMatch[1]}`;
    }
    const parsed = new Date(value);
    if (!Number.isNaN(parsed.getTime())) {
      return parsed.toLocaleDateString("es-CL");
    }
  }
  return "—";
}
