import { useState } from "react"
import { Link } from "react-router-dom"
import api from "../services/api"
import "./Login.css"

function Register() {
    const [username, setUsername] = useState("")
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [confirmPassword, setConfirmPassword] = useState("")
    const [errorMessage, setErrorMessage] = useState("")
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [isRegistered, setIsRegistered] = useState(false)

    async function handleSubmit(event) {
        event.preventDefault()
        setErrorMessage("")

        if (password !== confirmPassword) {
            setErrorMessage("As senhas não coincidem.")
            return
        }

        setIsSubmitting(true)
        try {
            await api.post("/auth/register", { username, email, password })
            setIsRegistered(true)
        } catch (error) {
            const responseData = error.response?.data
            setErrorMessage(
                responseData?.errors?.email
                || Object.values(responseData?.errors || {})[0]
                || responseData?.message
                || "Não foi possível criar a conta. Confira os dados e tente novamente."
            )
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <main className="login-container">
            <section className="login-box">
                <p className="eyebrow">PORTAL INTERNO</p>
                <h1>Criar sua conta</h1>
                <p className="login-intro">Cadastre-se para acompanhar e criar solicitações internas.</p>
                {isRegistered ? (
                    <div className="registration-success" role="status">
                        <p>Cadastro concluído. Agora você já pode entrar.</p>
                        <Link className="registration-link-button" to="/login">Ir para login</Link>
                    </div>
                ) : (
                    <form onSubmit={handleSubmit}>
                        <label htmlFor="register-name">Nome</label>
                        <input
                            id="register-name"
                            type="text"
                            autoComplete="name"
                            maxLength="100"
                            required
                            value={username}
                            onChange={(event) => setUsername(event.target.value)}
                        />
                        <label htmlFor="register-email">Email</label>
                        <input
                            id="register-email"
                            type="email"
                            autoComplete="email"
                            required
                            value={email}
                            onChange={(event) => setEmail(event.target.value)}
                        />
                        <label htmlFor="register-password">Senha</label>
                        <input
                            id="register-password"
                            type="password"
                            autoComplete="new-password"
                            minLength="8"
                            maxLength="20"
                            required
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                        />
                        <label htmlFor="confirm-password">Confirmar senha</label>
                        <input
                            id="confirm-password"
                            type="password"
                            autoComplete="new-password"
                            minLength="8"
                            maxLength="20"
                            required
                            value={confirmPassword}
                            onChange={(event) => setConfirmPassword(event.target.value)}
                        />
                        {errorMessage && <p className="form-error" role="alert">{errorMessage}</p>}
                        <button type="submit" disabled={isSubmitting}>
                            {isSubmitting ? "Cadastrando..." : "Criar conta"}
                        </button>
                    </form>
                )}
                {!isRegistered && (
                    <p className="register-prompt">Já tem uma conta? <Link to="/login">Entrar</Link></p>
                )}
            </section>
        </main>
    )
}

export default Register