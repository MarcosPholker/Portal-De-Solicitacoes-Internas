# Portal de Solicitações Internas

Sistema web desenvolvido como parte de desafio técnico para a vaga de Desenvolvedor(a) de Sistemas Júnior.

A aplicação permite que usuários autenticados registrem e acompanhem solicitações internas, com gerenciamento de categorias, status, filtros e indicadores através de um dashboard.

## Funcionalidades

- Autenticação de usuários
- Login e logout
- Autenticação baseada em JWT
- Cadastro de usuários
- Cadastro de solicitações internas
- Edição de solicitações
- Exclusão de solicitações
- Consulta de detalhes
- Alteração de status
- Filtros por período, categoria, status e título
- Dashboard com indicadores
- Controle de acesso às solicitações
- Validação dos dados enviados à API
- Tratamento de erros da aplicação

## Categorias

- TI
- RH
- Compras
- Financeiro
- Infraestrutura

## Status

- Aberto
- Em Atendimento
- Concluído

## Tecnologias utilizadas

### Backend

- Java 21
- Spring Boot 4.1.1
- Spring Data JPA
- Spring Security
- JWT
- Bean Validation
- Maven

### Frontend

- React 19
- Vite
- React Router
- Axios
- JavaScript
- CSS

### Banco de dados

- PostgreSQL

## Arquitetura

O projeto está dividido em duas partes principais:

```text
Portal-De-Solicitacoes-Internas/
├── backend/    # Backend - Spring Boot
└── frontend/            # Frontend - React
```

O frontend realiza requisições HTTP para a API desenvolvida em Spring Boot.

O backend é responsável pelas regras de negócio, autenticação, validações e persistência dos dados no PostgreSQL.

## Estrutura do Backend

O backend segue uma organização baseada em camadas, separando responsabilidades entre:

- Controllers
- Services
- Repositories
- Entities
- DTOs
- Exceptions
- Security
- Configurações

Essa organização busca manter o código separado por responsabilidade e facilitar a manutenção e evolução da aplicação.

## Autenticação

A aplicação utiliza Spring Security com autenticação baseada em JWT.

Após o login, a API gera um token que deve ser enviado nas requisições protegidas através do header:

```http
Authorization: Bearer <token>
```

O backend valida o token antes de permitir o acesso aos recursos protegidos.

## Banco de dados

O sistema utiliza PostgreSQL para persistência dos dados.

A conexão do backend utiliza as seguintes variáveis de ambiente:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

O banco utilizado pelo projeto é `portal_solicitacoes`.

## Pré-requisitos

Para executar o projeto utilizando Docker, é necessário ter instalado:

- Git
- Docker
- Docker Compose

O ambiente Docker disponibiliza o PostgreSQL, o backend e o frontend da aplicação.

## Executando com Docker

### 1. Clonar o projeto

```bash
git clone https://github.com/MarcosPholker/Portal-De-Solicitacoes-Internas.git
cd Portal-De-Solicitacoes-Internas
```

### 2. Configurar as variáveis de ambiente

Crie o arquivo `.env` na raiz do projeto a partir do arquivo `.env.example`:

Windows:

```powershell
copy .env.example .env
```

Linux/macOS:

```bash
cp .env.example .env
```

O `.env.example` contém valores de exemplo para permitir a execução do projeto. Em um ambiente real, utilize credenciais e uma chave JWT próprias.

Exemplo:

```env
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=dev_example_secret_change_this_key_9f7K2mQ8xL4pN6vR3tY5wE1sA0cD8hG7
```

> O arquivo `.env` não deve ser versionado no GitHub. Utilize o `.env.example` apenas como modelo de configuração.

### 3. Construir a imagem do frontend

O `docker-compose.yml` utiliza a imagem `portal-solicitacoes-frontend` para o frontend. Caso essa imagem ainda não exista localmente, construa-a a partir do diretório do frontend:

```bash
docker build -t portal-solicitacoes-frontend ./frontend
```

### 4. Subir a aplicação

Na raiz do projeto, execute:

```bash
docker compose up --build -d
```

O parâmetro `--build` garante que a imagem do backend seja reconstruída quando necessário.

Para verificar o estado dos containers:

```bash
docker compose ps
```

Para acompanhar os logs do backend:

```bash
docker compose logs -f backend
```

### 5. Acessar a aplicação

Com os containers em execução:

- **Frontend:** http://localhost:3000
- **Backend:** http://localhost:8080
- **PostgreSQL:** localhost:5432

### Parando os containers

Para parar os serviços:

```bash
docker compose down
```

O volume `postgres_data` é mantido ao utilizar apenas `docker compose down`, preservando os dados do banco.

Para remover também o volume e apagar os dados persistidos do PostgreSQL:

```bash
docker compose down -v
```

> Utilize `docker compose down -v` somente quando realmente quiser recriar o banco do zero.

### Reconstruindo após alterações

Quando houver alterações no backend ou nas imagens Docker, utilize:

```bash
docker compose down
docker compose up --build -d
```

Assim, os containers são recriados utilizando as versões atualizadas das imagens.

## Executando manualmente

Também é possível executar o backend e o frontend sem Docker.

### Backend

Entre no diretório do backend:

```bash
cd backend
```

Execute a aplicação:

```bash
mvn spring-boot:run
```

A API estará disponível em:

```text
http://localhost:8080
```

### Frontend

Entre no diretório do frontend:

```bash
cd frontend
```

Instale as dependências:

```bash
npm install
```

Execute o projeto:

```bash
npm run dev
```

O frontend será disponibilizado pelo Vite, normalmente em:

```text
http://localhost:5173
```

## Principais endpoints

### Autenticação

```http
POST /auth/register
POST /auth/login
```

### Solicitações

```http
GET    /internalrequest
GET    /internalrequest/{id}
POST   /internalrequest
PUT    /internalrequest/{id}
DELETE /internalrequest/delete/{id}
```
## Documentação da API

A API possui documentação interativa através do Swagger/OpenAPI.

Após iniciar a aplicação, acesse:

http://localhost:8080/swagger-ui/index.html

### Filtros

A listagem permite utilizar parâmetros para filtrar as solicitações, como categoria, status, período e título.

Exemplo:

```http
GET /internalrequest?category=TI
```

## Dashboard

O sistema disponibiliza indicadores relacionados às solicitações:

- Quantidade total
- Solicitações abertas
- Solicitações em atendimento
- Solicitações concluídas

## Segurança

As operações protegidas exigem autenticação.

Além da autenticação utilizando JWT, o backend realiza verificações relacionadas ao usuário autenticado antes de permitir operações sobre solicitações.

Por exemplo, operações de edição e exclusão verificam se a solicitação pertence ao usuário que está realizando a operação.

## Tratamento de erros

A aplicação possui tratamento de exceções para situações como:

- Usuário não encontrado
- Solicitação não encontrada
- Usuário não autenticado
- Acesso não autorizado
- Dados inválidos
- E-mail já cadastrado
- Credenciais inválidas

## Variáveis de ambiente

| Variável | Descrição |
|---|---|
| `DB_USERNAME` | Usuário do PostgreSQL |
| `DB_PASSWORD` | Senha do PostgreSQL |
| `JWT_SECRET` | Chave utilizada para geração e validação dos tokens JWT |

## Melhorias futuras

As funcionalidades abaixo não fazem parte do escopo obrigatório do desafio, mas foram identificadas como possíveis evoluções do sistema:

- Recuperação e alteração de senha
- Controle de permissões por perfil
- Paginação da listagem de solicitações
- Testes automatizados com maior cobertura
- Documentação da API com OpenAPI/Swagger
- Logs estruturados
- Pipeline de CI/CD
- Deploy em ambiente cloud

## Objetivo do projeto

O projeto foi desenvolvido com foco em demonstrar conhecimentos de desenvolvimento full stack e containerização, incluindo:

- Desenvolvimento de APIs REST
- Desenvolvimento de interfaces web
- Persistência de dados
- Autenticação e segurança
- Modelagem de dados
- Organização em camadas
- Regras de negócio
- Consumo de APIs
- Documentação técnica

## Autor

**Marcos Antônio Leite da Silva**

Projeto desenvolvido como parte de processo seletivo para Desenvolvedor(a) de Sistemas Júnior.
