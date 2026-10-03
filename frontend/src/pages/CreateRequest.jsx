import { useState } from "react"
import { useEffect } from "react"
import { Link, useNavigate, useParams } from "react-router-dom"
import api from "../services/api"

function CreateRequest() {
    const { id } = useParams()
    const navigate = useNavigate()
    const [title, setTitle] = useState("")
    const [category, setCategory] = useState("")
    const [description, setDescription] = useState("")
    const [requestStatus, setRequestStatus] = useState("")
    const [errorMessage, setErrorMessage] = useState("")
    const [isLoading, setIsLoading] = useState(Boolean(id))
    const [isSubmitting, setIsSubmitting] = useState(false)

    useEffect(() => {
        if (!id) return
        api.get(`/internalrequest/${id}`)
            .then(({ data }) => {
                setTitle(data.title || "")
                setCategory(data.internalRequestCategory || "")
                setDescription(data.description || "")
                setRequestStatus(data.internalRequestStatus || "")
            })
            .catch(() => setErrorMessage("Não foi possível carregar esta solicitação."))
            .finally(() => setIsLoading(false))
    }, [id])

    async function handleSubmit(event) {
        event.preventDefault()
        setErrorMessage("")
        setIsSubmitting(true)
        const requestData = {
            title,
            description,
            internalRequestCategory: category,
            ...(id && { internalRequestStatus: requestStatus })
        }

        try {
            if (id) {
                await api.put(`/internalrequest/update/${id}`, requestData)
            } else {
                await api.post("/internalrequest/create", requestData)
            }
            navigate("/internalrequest")
        } catch (error) {
            setErrorMessage(error.response?.data?.message || "Não foi possível salvar. Confira os dados e tente novamente.")
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <div className="requests-container">
            <header className="topbar">
                <Link className="brand" to="/dashboard">SI<span>/</span></Link>
                <Link className="topbar-link" to="/dashboard">Dashboard</Link>
                <Link className="topbar-link" to="/internalrequest">Todas</Link>
                <Link className="topbar-link" to="/internalrequest/mine">Meus pedidos</Link>
                <span className="topbar-caption">Portal interno</span>
                <Link className="text-button" to="/internalrequest">Voltar à lista</Link>
            </header>
            <main className="form-main">
                <p className="eyebrow">EQUIPE · SOLICITAÇÕES</p>
                <h1>{id ? "Editar solicitação" : "Nova solicitação"}</h1>
                <p className="page-subtitle">Descreva o pedido para que a equipe possa encaminhá-lo.</p>
                {isLoading ? <p role="status">Carregando solicitação...</p> : (
                    <form className="request-form" onSubmit={handleSubmit}>
                        <label htmlFor="request-title">Título</label>
                        <input id="request-title" type="text" maxLength="100" required value={title} onChange={(event) => setTitle(event.target.value)} />
                        <label htmlFor="request-description">Descrição</label>
                        <textarea id="request-description" maxLength="500" rows="5" required value={description} onChange={(event) => setDescription(event.target.value)} />
                        <span className="character-count">{description.length}/500</span>
                        <label htmlFor="request-category">Categoria</label>
                        <select id="request-category" required value={category} onChange={(event) => setCategory(event.target.value)}>
                            <option value="">Selecione uma categoria</option>
                            <option value="TI">Tecnologia</option>
                            <option value="RH">Recursos Humanos</option>
                            <option value="SALES">Vendas</option>
                            <option value="FINANCIAL">Financeiro</option>
                            <option value="INFRASTRUCTURE">Infraestrutura</option>
                        </select>
                        {id && (
                            <>
                                <label htmlFor="request-status">Status</label>
                                <select id="request-status" required value={requestStatus} onChange={(event) => setRequestStatus(event.target.value)}>
                                    <option value="OPEN">Aberta</option>
                                    <option value="IN_PROGRESS">Em andamento</option>
                                    <option value="COMPLETED">Concluída</option>
                                </select>
                            </>
                        )}
                        {errorMessage && <p className="form-error" role="alert">{errorMessage}</p>}
                        <div className="form-actions">
                            <Link className="secondary-link" to="/internalrequest">Cancelar</Link>
                            <button type="submit" className="primary-button" disabled={isSubmitting || isLoading}>
                                {isSubmitting ? "Salvando..." : id ? "Salvar alterações" : "Criar solicitação"}
                            </button>
                        </div>
                    </form>
                )}
            </main>
        </div>
    )
}

export default CreateRequest