import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "../pages/Login";
import Home from "../pages/Home";
import RecoveryLocked from "../pages/recovery/RecoveryLocked";
import RecoveryForgot from "../pages/recovery/RecoveryForgot";
import RecoveryCode from "../pages/recovery/RecoveryCode";
import RecoveryNewPassword from "../pages/recovery/RecoveryNewPassword";
import RecoveryConfirm from "../pages/recovery/RecoveryConfirm";
import PrivateRoute from "../components/PrivateRoute";
import AdminCompaniesList from "../pages/AdminCompaniesList";
import AdminRegisterCompany from "../pages/AdminRegisterCompany";
import AdminEditCompany from "../pages/AdminEditCompany";
import AdminRecruitersList from "../pages/AdminRecruitersList";
import AdminRegisterRecruiter from "../pages/AdminRegisterRecruiter";
import AdminEditRecruiter from "../pages/AdminEditRecruiter";
import AdminSettingsPlaceholder from "../pages/AdminSettingsPlaceholder";
import AdminJobsList from "../pages/AdminJobsList";
import AdminJobDetail from "../pages/AdminJobDetail";
import RecruiterHome from "../pages/RecruiterHome";
import RecruiterReclutamiento from "../pages/RecruiterReclutamiento";
import RecruiterJobsList from "../pages/RecruiterJobsList";
import RecruiterJobCreate from "../pages/RecruiterJobCreate";
import RecruiterJobDetail from "../pages/RecruiterJobDetail";
import RecruiterJobEdit from "../pages/RecruiterJobEdit";
import RecruiterApplicationView from "../pages/RecruiterApplicationView";
import RecruiterCandidateProfile from "../pages/RecruiterCandidateProfile";

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
              <AdminCompaniesList />
            </PrivateRoute>
          }
        />
        <Route
          path="/companies/nueva"
          element={
            <PrivateRoute roles={["ADMIN"]}>
              <AdminRegisterCompany />
            </PrivateRoute>
          }
        />
        <Route
          path="/companies/:companyId/editar"
          element={
            <PrivateRoute roles={["ADMIN"]}>
              <AdminEditCompany />
            </PrivateRoute>
          }
        />
        <Route
          path="/recruiters"
          element={
            <PrivateRoute roles={["ADMIN"]}>
              <AdminRecruitersList />
            </PrivateRoute>
          }
        />
        <Route
          path="/recruiters/nueva"
          element={
            <PrivateRoute roles={["ADMIN"]}>
              <AdminRegisterRecruiter />
            </PrivateRoute>
          }
        />
        <Route
          path="/recruiters/:userId/editar"
          element={
            <PrivateRoute roles={["ADMIN"]}>
              <AdminEditRecruiter />
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
          path="/ofertas"
          element={
            <PrivateRoute roles={["ADMIN"]}>
              <AdminJobsList />
            </PrivateRoute>
          }
        />
        <Route
          path="/ofertas/:jobId/editar"
          element={
            <PrivateRoute roles={["ADMIN"]}>
              <RecruiterJobEdit adminMode />
            </PrivateRoute>
          }
        />
        <Route
          path="/ofertas/:jobId"
          element={
            <PrivateRoute roles={["ADMIN"]}>
              <AdminJobDetail />
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
          path="/recruiter/ofertas/:jobId/postulaciones/:applicationId/perfil"
          element={
            <PrivateRoute roles={["RECRUITER"]}>
              <RecruiterCandidateProfile />
            </PrivateRoute>
          }
        />
        <Route
          path="/recruiter/ofertas/:jobId/postulaciones/:applicationId/*"
          element={
            <PrivateRoute roles={["RECRUITER"]}>
              <RecruiterApplicationView />
            </PrivateRoute>
          }
        />
        <Route
          path="/recruiter/ofertas/:jobId/editar"
          element={
            <PrivateRoute roles={["RECRUITER"]}>
              <RecruiterJobEdit />
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
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default AppRoutes;
