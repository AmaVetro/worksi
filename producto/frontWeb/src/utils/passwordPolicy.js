const POLICY =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]).{10,}$/;

export function passwordMatches(password) {
  return typeof password === "string" && POLICY.test(password);
}
