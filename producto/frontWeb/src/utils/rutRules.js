export function normalizeRut(raw) {
  if (raw == null || typeof raw !== "string") {
    return "";
  }
  return raw.trim().replace(/\./g, "");
}

export function isValidChileRut(raw) {
  const n = normalizeRut(raw);
  if (!n) {
    return false;
  }
  const dash = n.lastIndexOf("-");
  if (dash < 1 || dash !== n.length - 2) {
    return false;
  }
  const body = n.slice(0, dash);
  const dv = n.slice(dash + 1);
  if (!/^[0-9]{7,8}$/.test(body) || !/^[0-9kK]$/.test(dv)) {
    return false;
  }
  let factor = 2;
  let sum = 0;
  for (let i = body.length - 1; i >= 0; i--) {
    const digit = Number(body[i]);
    if (Number.isNaN(digit)) {
      return false;
    }
    sum += digit * factor;
    factor = factor === 7 ? 2 : factor + 1;
  }
  const rest = 11 - (sum % 11);
  let expected;
  if (rest === 11) {
    expected = "0";
  } else if (rest === 10) {
    expected = "K";
  } else {
    expected = String(rest);
  }
  const got = dv[0].toUpperCase();
  return expected === got;
}
