import { Navigate } from "react-router-dom";
import { normalizeRole } from "../services/authService";

function readUser() {
  try {
    const raw = localStorage.getItem("user");
    if (!raw) return null;
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

export function PrivateRoute({ children, roles }) {
  const token = localStorage.getItem("token");
  const user = readUser();
  if (!token || !user) {
    return <Navigate to="/" replace />;
  }
  const role = normalizeRole(user.role);
  if (!role) {
    return <Navigate to="/" replace />;
  }
  if (roles && roles.length > 0 && !roles.includes(role)) {
    if (role === "RECRUITER") {
      return <Navigate to="/recruiter/home" replace />;
    }
    if (role === "ADMIN") {
      return <Navigate to="/home" replace />;
    }
    return <Navigate to="/" replace />;
  }
  return children;
}

export default PrivateRoute;
