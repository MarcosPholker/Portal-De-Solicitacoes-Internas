import { useEffect, useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import api from "../services/api"
import "./Request.css"
import "./Dashboard.css"

function Dashboard() {
    const [summary, setSummary] = useState(null)
    const [errorMessage, setErrorMessage] = useState("")
    const [isLoading, setIsLoading] = useState(true)
    const navigate = useNavigate()

    useEffect(() => {
        api.get("/internalrequest/dashboard")
            .then(({ data }) => setSummary(data))
            .catch((error) => {
                if (error.response?.status === 401) {
                    setErrorMessage("Sua sessão não foi autorizada. Entre novamente para carregar o dashboard.")
                    return
                }
                setErrorMessage("Não foi possível carregar o dashboard. Tente novamente.")
            })
            .finally(() => setIsLoading(false))
    }, [])

    function handleLogout() {
        localStorage.removeItem("token")
        navigate("/login", { replace: true })
    }

    const metrics = [
        { key: "total", label: "Total de solicitações", value: summary?.total, className: "metric-total" },
        { key: "open", label: "Abertas", value: summary?.open, className: "metric-open" },
        { key: "inProgress", label: "Em andamento", value: summary?.inProgress, className: "metric-progress" },
        { key: "completed", label: "Concluídas", value: summary?.completed, className: "metric-completed" }
    ]

    return (
        <div className="requests-container">
            <header className="topbar">
                <Link className="brand" to="/dashboard">SI<span>/</span></Link>
                <Link className="topbar-link topbar-link-active" to="/dashboard">Dashboard</Link>
                <Link className="topbar-link" to="/internalrequest">Solicitações</Link>
                <button className="text-button" type="button" onClick={handleLogout}>Sair</button>
            </header>

            <main className="dashboard-main">
                <div className="dashboard-heading">
                    <div>
                        <p className="eyebrow">VISÃO GERAL</p>
                        <h1>Dashboard</h1>
                        <p className="page-subtitle">Acompanhe o andamento das solicitações internas.</p>
                    </div>
                    <Link className="primary-link" to="/internalrequest/create">+ Nova solicitação</Link>
                </div>

                {errorMessage && (
                    <div className="dashboard-error" role="alert">
                        <p>{errorMessage}</p>
                        {errorMessage.includes("Entre novamente") && <Link to="/login">Ir para login</Link>}
                    </div>
                )}

                <section aria-label="Resumo das solicitações">
                    <div className="metric-grid">
                        {metrics.map((metric) => (
                            <article className={`metric-card ${metric.className}`} key={metric.key}>
                                <p>{metric.label}</p>
                                <strong aria-live="polite">
                                    {isLoading ? "..." : metric.value ?? 0}
                                </strong>
                            </article>
                        ))}
                    </div>
                    {!isLoading && !errorMessage && summary?.total === 0 && (
                        <p className="dashboard-empty">Ainda não há solicitações registradas.</p>
                    )}
                </section>

                <section className="dashboard-actions" aria-label="Acesso rápido">
                    <h2>Acesso rápido</h2>
                    <div className="dashboard-action-links">
                        <Link to="/internalrequest">Ver todas as solicitações <span aria-hidden="true">→</span></Link>
                        <Link to="/internalrequest/mine">Ver meus pedidos <span aria-hidden="true">→</span></Link>
                    </div>
                </section>
            </main>
        </div>
    )
}

export default Dashboard