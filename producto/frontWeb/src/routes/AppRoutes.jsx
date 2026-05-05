import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "../pages/Login";
import Register from "../pages/Register";
import Home from "../pages/Home";

function PrivateRoute({ children }) {
  return children; // 🔥 deja pasar todo
}

    function AppRoutes() {
    return (
        <BrowserRouter>
        <Routes>
            <Route path="/" element={<Login />} />
            <Route path="/register" element={<Register />} />

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