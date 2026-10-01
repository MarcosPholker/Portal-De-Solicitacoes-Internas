import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom"

import Login from "./pages/Login"
import Requests from "./pages/Request"
import CreateRequest from "./pages/CreateRequest"
import Dashboard from "./pages/Dashboard"
function ProtectedRoute({ children }) {
    return localStorage.getItem("token")
        ? children
        : <Navigate to="/login" replace />
}


function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Navigate to="/dashboard" replace />} />
                <Route path="/login" element={<Login />} />
                <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
                <Route path="/internalrequest" element={<ProtectedRoute><Requests /></ProtectedRoute>} />
                                <Route path="/internalrequest/mine" element={<ProtectedRoute><Requests mineOnly /></ProtectedRoute>} />
                <Route path="/internalrequest/create" element={<ProtectedRoute><CreateRequest /></ProtectedRoute>} />
                <Route path="/internalrequest/:id/edit" element={<ProtectedRoute><CreateRequest /></ProtectedRoute>} />
                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App
