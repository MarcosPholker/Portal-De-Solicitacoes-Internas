import { useEffect, useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import api from "../services/api"
import "./Request.css"

function Request() {
    const [requests, setRequests] = useState([])
    const [filters, setFilters] = useState({ title: "", category: "", status: "" })
    const [appliedFilters, setAppliedFilters] = useState({ title: "", category: "", status: "" })
    const [isLoading, setIsLoading] = useState(true)
    const [errorMessage, setErrorMessage] = useState("")
    const [isDeleting, setIsDeleting] = useState("")
    const navigate = useNavigate()

    useEffect(() => {
        async function fetchRequests() {
            setIsLoading(true)
            setErrorMessage("")
            try {
                const params = Object.fromEntries(
                    Object.entries(appliedFilters).filter(([, value]) => value)
                )
                const response = await api.get("/internalrequest", { params })
                setRequests(response.data)
            } catch (error) {
                if (error.response?.status === 401) {
                    localStorage.removeItem("token")
                    navigate("/login", { replace: true })
                    return
                }
                if (error.response?.status === 403) {
                    setErrorMessage("Você não tem permissão para visualizar estas solicitações.")
                    return
                }
                setErrorMessage("Não foi possível carregar as solicitações. Tente novamente.")
            } finally {
                setIsLoading(false)
            }
        }

        fetchRequests()
    }, [appliedFilters, navigate])

    function handleFilterChange(event) {
        setFilters({ ...filters, [event.target.name]: event.target.value })
    }

    function handleFilterSubmit(event) {
        event.preventDefault()
        setAppliedFilters({ ...filters })
    }

    async function handleDelete(requestId) {
        if (!window.confirm("Excluir esta solicitação? Esta ação não pode ser desfeita.")) return
        setIsDeleting(requestId)
        setErrorMessage("")
        try {
            await api.delete(`/internalrequest/delete/${requestId}`)
            setRequests((current) => current.filter((request) => request.id !== requestId))
        } catch {
            setErrorMessage("Não foi possível excluir a solicitação. Tente novamente.")
        } finally {
            setIsDeleting("")
        }
    }

    function handleLogout() {
        localStorage.removeItem("token")
        navigate("/login", { replace: true })
    }

    const categoryLabels = {
        TI: "Tecnologia",
        RH: "Recursos Humanos",
        SALES: "Vendas",
        FINANCIAL: "Financeiro",
        INFRASTRUCTURE: "Infraestrutura"
    }
    const statusLabels = {
        OPEN: "Aberta",
        IN_PROGRESS: "Em andamento",
        COMPLETED: "Concluída"
    }

    return (
        <div className="requests-container">
            <header className="topbar">
                <Link className="brand" to="/internalrequest">SI<span>/</span></Link>
                <span className="topbar-caption">Portal interno</span>
                <button className="text-button" type="button" onClick={handleLogout}>Sair</button>
            </header>
            <main className="requests-main">
                <div className="page-heading">
                    <div>
                        <p className="eyebrow">EQUIPE · SOLICITAÇÕES</p>
                        <h1>Pedidos internos</h1>
                        <p className="page-subtitle">Acompanhe e organize as solicitações da equipe.</p>
                    </div>
                    <Link className="primary-link" to="/internalrequest/create">+ Nova solicitação</Link>
                </div>

                <form className="filters" onSubmit={handleFilterSubmit}>
                    <label className="filter-search" htmlFor="filter-title">
                        Buscar por título
                        <input id="filter-title" name="title" value={filters.title} onChange={handleFilterChange} placeholder="Ex.: acesso ao sistema" />
                    </label>
                    <label htmlFor="filter-category">
                        Categoria
                        <select id="filter-category" name="category" value={filters.category} onChange={handleFilterChange}>
                            <option value="">Todas</option>
                            <option value="TI">Tecnologia</option>
                            <option value="RH">Recursos Humanos</option>
                            <option value="SALES">Vendas</option>
                            <option value="FINANCIAL">Financeiro</option>
                            <option value="INFRASTRUCTURE">Infraestrutura</option>
                        </select>
                    </label>
                    <label htmlFor="filter-status">
                        Status
                        <select id="filter-status" name="status" value={filters.status} onChange={handleFilterChange}>
                            <option value="">Todos</option>
                            <option value="OPEN">Aberta</option>
                            <option value="IN_PROGRESS">Em andamento</option>
                            <option value="COMPLETED">Concluída</option>
                        </select>
                    </label>
                    <button type="submit" className="filter-button">Filtrar</button>
                </form>

                {errorMessage && <p className="form-error page-error" role="alert">{errorMessage}</p>}
                <div className="list-heading">
                    <h2>Solicitações</h2>
                    {!isLoading && <span>{requests.length} {requests.length === 1 ? "pedido" : "pedidos"}</span>}
                </div>

                {isLoading ? (
                    <p className="list-message" role="status">Carregando solicitações...</p>
                ) : requests.length === 0 ? (
                    <div className="list-message empty-state">
                        <h3>Nenhum pedido encontrado</h3>
                        <p>Experimente ajustar os filtros ou crie uma nova solicitação.</p>
                    </div>
                ) : (
                    <div className="request-table-wrap">
                        <table className="request-table">
                            <thead>
                                <tr><th>Solicitação</th><th>Categoria</th><th>Solicitante</th><th>Data</th><th>Status</th><th><span className="sr-only">Ações</span></th></tr>
                            </thead>
                            <tbody>
                                {requests.map((request) => (
                                    <tr key={request.id}>
                                        <td className="request-title-cell">{request.title}</td>
                                        <td>{categoryLabels[request.internalRequestCategory] || request.internalRequestCategory}</td>
                                        <td>{request.username || "-"}</td>
                                        <td>{request.creationDate ? new Date(request.creationDate).toLocaleDateString("pt-BR") : "-"}</td>
                                        <td><span className={`status-badge status-${request.internalRequestStatus?.toLowerCase()}`}>{statusLabels[request.internalRequestStatus] || request.internalRequestStatus}</span></td>
                                        <td className="row-actions">
                                            <Link to={`/internalrequest/${request.id}/edit`} aria-label={`Editar ${request.title}`}>Editar</Link>
                                            <button type="button" onClick={() => handleDelete(request.id)} disabled={isDeleting === request.id} aria-label={`Excluir ${request.title}`}>
                                                {isDeleting === request.id ? "Excluindo..." : "Excluir"}
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </main>
        </div>
    )
}

export default Request