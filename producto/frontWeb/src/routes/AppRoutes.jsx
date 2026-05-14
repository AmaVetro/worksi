import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "../pages/Login";
import Home from "../pages/Home";
import RecoveryLocked from "../pages/recovery/RecoveryLocked";
import RecoveryForgot from "../pages/recovery/RecoveryForgot";
import RecoveryCode from "../pages/recovery/RecoveryCode";
import RecoveryNewPassword from "../pages/recovery/RecoveryNewPassword";
import RecoveryConfirm from "../pages/recovery/RecoveryConfirm";
import PrivateRoute from "../components/PrivateRoute";
import AdminRegisterCompany from "../pages/AdminRegisterCompany";
import AdminRegisterRecruiter from "../pages/AdminRegisterRecruiter";
import AdminSettingsPlaceholder from "../pages/AdminSettingsPlaceholder";
import RecruiterHome from "../pages/RecruiterHome";
import RecruiterReclutamiento from "../pages/RecruiterReclutamiento";
import RecruiterJobsList from "../pages/RecruiterJobsList";
import RecruiterJobCreate from "../pages/RecruiterJobCreate";
import RecruiterJobDetail from "../pages/RecruiterJobDetail";
import RecruiterPostulacionesPlaceholder from "../pages/RecruiterPostulacionesPlaceholder";

function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/recovery/locked" element={<RecoveryLocked />} />
        <Route path="/recovery/forgot" element={<RecoveryForgot />} />
        <Route path="/recovery/code" element={<RecoveryCode />} />
        <Route path="/recovery/new-password" element={<RecoveryNewPassword />} />
        <Route path="/recovery/confirm" element={<RecoveryConfirm />} />
        <Route
          path="/home"
          element={
            <PrivateRoute roles={["ADMIN"]}>
              <Home />
            </PrivateRoute>
          }
        />
        <Route
          path="/companies"
          element={
            <PrivateRoute roles={["ADMIN"]}>
              <AdminRegisterCompany />
            </PrivateRoute>
          }
        />
        <Route
          path="/recruiters"
          element={
            <PrivateRoute roles={["ADMIN"]}>
              <AdminRegisterRecruiter />
            </PrivateRoute>
          }
        />
        <Route
          path="/settings"
          element={
            <PrivateRoute roles={["ADMIN"]}>
              <AdminSettingsPlaceholder />
            </PrivateRoute>
          }
        />
        <Route
          path="/recruiter/home"
          element={
            <PrivateRoute roles={["RECRUITER"]}>
              <RecruiterHome />
            </PrivateRoute>
          }
        />
        <Route
          path="/recruiter/reclutamiento"
          element={
            <PrivateRoute roles={["RECRUITER"]}>
              <RecruiterReclutamiento />
            </PrivateRoute>
          }
        />
        <Route
          path="/recruiter/ofertas"
          element={
            <PrivateRoute roles={["RECRUITER"]}>
              <RecruiterJobsList />
            </PrivateRoute>
          }
        />
        <Route
          path="/recruiter/ofertas/nueva"
          element={
            <PrivateRoute roles={["RECRUITER"]}>
              <RecruiterJobCreate />
            </PrivateRoute>
          }
        />
        <Route
          path="/recruiter/ofertas/:jobId"
          element={
            <PrivateRoute roles={["RECRUITER"]}>
              <RecruiterJobDetail />
            </PrivateRoute>
          }
        />
        <Route
          path="/recruiter/postulaciones"
          element={
            <PrivateRoute roles={["RECRUITER"]}>
              <RecruiterPostulacionesPlaceholder />
            </PrivateRoute>
          }
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default AppRoutes;
