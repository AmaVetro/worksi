import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "../pages/Login";
import Register from "../pages/Register";
import Home from "../pages/Home";
import RecoveryLocked from "../pages/recovery/RecoveryLocked";
import RecoveryForgot from "../pages/recovery/RecoveryForgot";
import RecoveryCode from "../pages/recovery/RecoveryCode";
import RecoveryNewPassword from "../pages/recovery/RecoveryNewPassword";
import RecoveryConfirm from "../pages/recovery/RecoveryConfirm";

function PrivateRoute({ children }) {
  return children;
}

function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/recovery/locked" element={<RecoveryLocked />} />
        <Route path="/recovery/forgot" element={<RecoveryForgot />} />
        <Route path="/recovery/code" element={<RecoveryCode />} />
        <Route path="/recovery/new-password" element={<RecoveryNewPassword />} />
        <Route path="/recovery/confirm" element={<RecoveryConfirm />} />
        <Route
          path="/home"
          element={
            <PrivateRoute>
              <Home />
            </PrivateRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default AppRoutes;