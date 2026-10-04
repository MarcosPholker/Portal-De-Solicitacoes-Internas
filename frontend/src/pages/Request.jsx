import { Fragment, useEffect, useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import api from "../services/api"
import "./Request.css"

function Request({ mineOnly = false }) {
    const [requests, setRequests] = useState([])
    const [filters, setFilters] = useState({ title: "", category: "", status: "" })
    const [appliedFilters, setAppliedFilters] = useState({ title: "", category: "", status: "" })
    const [isLoading, setIsLoading] = useState(true)
    const [errorMessage, setErrorMessage] = useState("")
    const [isDeleting, setIsDeleting] = useState("")
    const [expandedRequestId, setExpandedRequestId] = useState(null)
    const [requestDetails, setRequestDetails] = useState({})
    const [isLoadingDetails, setIsLoadingDetails] = useState("")
    const [detailsError, setDetailsError] = useState("")
    const navigate = useNavigate()

    useEffect(() => {
        async function fetchRequests() {
            setIsLoading(true)
            setErrorMessage("")
            try {
                const params = Object.fromEntries(
                    Object.entries(appliedFilters).filter(([, value]) => value)
                )
                const endpoint = mineOnly ? "/internalrequest/mine" : "/internalrequest"
                const response = await api.get(endpoint, { params })
                setRequests(response.data)
            } catch (error) {
                if (error.response?.status === 401) {
                    console.error("Erro 401:", error.response);
                    return;
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
    }, [appliedFilters, mineOnly, navigate])

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

    async function handleToggleDetails(requestId) {
        if (expandedRequestId === requestId) {
            setExpandedRequestId(null)
            return
        }

        setExpandedRequestId(requestId)
        setDetailsError("")
        if (requestDetails[requestId]) return

        setIsLoadingDetails(requestId)
        try {
            const response = await api.get(`/internalrequest/${requestId}`)
            setRequestDetails((current) => ({ ...current, [requestId]: response.data }))
        } catch {
            setDetailsError("Não foi possível carregar os detalhes. Tente novamente.")
        } finally {
            setIsLoadingDetails("")
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
                <Link className="brand" to="/dashboard">SI<span>/</span></Link>
                <Link className="topbar-link" to="/dashboard">Dashboard</Link>
                <Link className={`topbar-link ${!mineOnly ? "topbar-link-active" : ""}`} to="/internalrequest">Todas</Link>
                <Link className={`topbar-link ${mineOnly ? "topbar-link-active" : ""}`} to="/internalrequest/mine">Meus pedidos</Link>
                <span className="topbar-caption">Portal interno</span>
                <button className="text-button" type="button" onClick={handleLogout}>Sair</button>
            </header>
            <main className="requests-main">
                <div className="page-heading">
                    <div>
                        <p className="eyebrow">EQUIPE · SOLICITAÇÕES</p>
                        <h1>{mineOnly ? "Meus pedidos" : "Pedidos internos"}</h1>
                        <p className="page-subtitle">{mineOnly ? "Acompanhe as solicitações que você registrou." : "Acompanhe e organize as solicitações da equipe."}</p>
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
                    <h2>{mineOnly ? "Seus pedidos" : "Solicitações"}</h2>
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
                                <tr>
                                    <th>Solicitação</th>
                                    <th>Categoria</th>
                                    <th>Solicitante</th>
                                    <th>Status</th>
                                    <th>Detalhes</th>
                                </tr>
                            </thead>
                            <tbody>
                                {requests.map((request) => (
                                    <Fragment key={request.id}>
                                        <tr>
                                            <td className="request-title-cell">{request.title}</td>
                                            <td>{categoryLabels[request.internalRequestCategory] || request.internalRequestCategory}</td>
                                            <td>{request.username || "-"}</td>
                                            <td><span className={`status-badge status-${request.internalRequestStatus?.toLowerCase()}`}>{statusLabels[request.internalRequestStatus] || request.internalRequestStatus}</span></td>
                                            <td>
                                                <button
                                                    className="details-toggle"
                                                    type="button"
                                                    onClick={() => handleToggleDetails(request.id)}
                                                    aria-expanded={expandedRequestId === request.id}
                                                    aria-controls={`request-details-${request.id}`}
                                                >
                                                    {expandedRequestId === request.id ? "Ocultar detalhes" : "Mostrar detalhes"}
                                                </button>
                                            </td>
                                        </tr>
                                        {expandedRequestId === request.id && (
                                            <tr id={`request-details-${request.id}`}>
                                                <td className="request-details-cell" colSpan="5">
                                                    {isLoadingDetails === request.id ? (
                                                        <p role="status">Carregando detalhes...</p>
                                                    ) : detailsError ? (
                                                        <p className="form-error" role="alert">{detailsError}</p>
                                                    ) : (
                                                        <div className="request-details-content">
                                                            <dl className="request-detail-grid">
                                                                <div>
                                                                    <dt>ID da solicitação</dt>
                                                                    <dd>{requestDetails[request.id]?.id || request.id}</dd>
                                                                </div>
                                                                <div>
                                                                    <dt>Título</dt>
                                                                    <dd>{requestDetails[request.id]?.title || request.title}</dd>
                                                                </div>
                                                                <div>
                                                                    <dt>Categoria</dt>
                                                                    <dd>{categoryLabels[requestDetails[request.id]?.internalRequestCategory] || requestDetails[request.id]?.internalRequestCategory || "-"}</dd>
                                                                </div>
                                                                <div>
                                                                    <dt>Data de criação</dt>
                                                                    <dd>{requestDetails[request.id]?.creationDate ? new Date(requestDetails[request.id].creationDate).toLocaleString("pt-BR") : "-"}</dd>
                                                                </div>
                                                                <div>
                                                                    <dt>Status</dt>
                                                                    <dd>{statusLabels[requestDetails[request.id]?.internalRequestStatus] || requestDetails[request.id]?.internalRequestStatus || "-"}</dd>
                                                                </div>
                                                                <div className="request-detail-description">
                                                                    <dt>Descrição</dt>
                                                                    <dd>{requestDetails[request.id]?.description || "-"}</dd>
                                                                </div>
                                                                <div>
                                                                    <dt>ID do solicitante</dt>
                                                                    <dd>{requestDetails[request.id]?.userResponseDTO?.id || "-"}</dd>
                                                                </div>
                                                                <div>
                                                                    <dt>Solicitante</dt>
                                                                    <dd>{requestDetails[request.id]?.userResponseDTO?.username || "-"}</dd>
                                                                </div>
                                                                <div>
                                                                    <dt>E-mail do solicitante</dt>
                                                                    <dd>{requestDetails[request.id]?.userResponseDTO?.email || "-"}</dd>
                                                                </div>
                                                            </dl>
                                                            {mineOnly && (
                                                                <div className="row-actions">
                                                                    <Link to={`/internalrequest/${request.id}/edit`}>Editar</Link>
                                                                    <button type="button" onClick={() => handleDelete(request.id)} disabled={isDeleting === request.id}>
                                                                        {isDeleting === request.id ? "Excluindo..." : "Excluir"}
                                                                    </button>
                                                                </div>
                                                            )}
                                                        </div>
                                                    )}
                                                </td>
                                            </tr>
                                        )}
                                    </Fragment>
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