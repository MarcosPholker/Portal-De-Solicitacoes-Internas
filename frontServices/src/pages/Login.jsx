import { useState } from "react"
import { useNavigate } from "react-router-dom"
import "./Login.css"
import api from "../services/api"

function Login() {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [errorMessage, setErrorMessage] = useState("")
    const [isSubmitting, setIsSubmitting] = useState(false)
    const navigate = useNavigate()

    async function handleSubmit(event) {
        event.preventDefault()
        setErrorMessage("")
        setIsSubmitting(true)

        try {
            const response = await api.post("/auth/login", { email, password })
            localStorage.setItem("token", response.data)
            navigate("/dashboard")
        } catch (error) {
            setErrorMessage(error.response?.data?.message || "Não foi possível entrar. Confira seu email e senha.")
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <main className="login-container">
            <section className="login-box">
                <p className="eyebrow">PORTAL INTERNO</p>
                <h1>Solicitações<br />da equipe</h1>
                <p className="login-intro">Entre para acompanhar e registrar pedidos internos.</p>
                <form onSubmit={handleSubmit}>
                    <label htmlFor="email">Email</label>
                    <input
                        id="email"
                        type="email"
                        autoComplete="username"
                        required
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                    />
                    <label htmlFor="password">Senha</label>
                    <input
                        id="password"
                        type="password"
                        autoComplete="current-password"
                        required
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                    />
                    {errorMessage && <p className="form-error" role="alert">{errorMessage}</p>}
                    <button type="submit" disabled={isSubmitting}>
                        {isSubmitting ? "Entrando..." : "Entrar"}
                    </button>
                </form>
            </section>
        </main>
    )
}

export default Login