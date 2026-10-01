# Portal de Solicitações Internas

Sistema web desenvolvido como parte de desafio técnico para a vaga de Desenvolvedor(a) de Sistemas Júnior.

A aplicação permite que usuários autenticados registrem e acompanhem solicitações internas, com gerenciamento de categorias, status, filtros e indicadores através de um dashboard.

## Funcionalidades

- Autenticação de usuários
- Login e logout
- Autenticação baseada em JWT
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

O projeto está dividido em três partes principais:

```text
Portal-De-Solicitacoes-Internas/
├── solicitacoes-internas/    # Backend - Spring Boot
├── frontServices/            # Frontend - React
└── database/                 # Scripts do banco de dados
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

Exemplo:

```text
DB_USERNAME=postgres
DB_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta
```

O projeto possui o diretório `database/` destinado aos scripts relacionados ao banco de dados.

## Pré-requisitos

Para executar o projeto localmente, é necessário ter instalado:

- Java 21
- Maven
- Node.js
- npm
- PostgreSQL

Também é necessário configurar as variáveis de ambiente utilizadas pelo backend.

## Executando o Backend

Entre no diretório do backend:

```bash
cd solicitacoes-internas
```

Execute a aplicação:

```bash
mvn spring-boot:run
```

A API estará disponível em:

```text
http://localhost:8080
```

## Executando o Frontend

Entre no diretório do frontend:

```bash
cd frontServices
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
DELETE /internalrequest/{id}
```

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

## Variáveis de ambiente

| Variável | Descrição |
|---|---|
| `DB_USERNAME` | Usuário do PostgreSQL |
| `DB_PASSWORD` | Senha do PostgreSQL |
| `JWT_SECRET` | Chave utilizada para geração e validação dos tokens JWT |

## Docker

A aplicação será disponibilizada também através de Docker Compose, permitindo executar os principais serviços do projeto de forma integrada.

A configuração de Docker será documentada nesta seção após a finalização dos arquivos de containerização.

## Melhorias futuras

As funcionalidades abaixo não fazem parte do escopo obrigatório do desafio, mas foram identificadas como possíveis evoluções do sistema:

- Cadastro de novos usuários pela interface
- Gerenciamento de usuários
- Recuperação e alteração de senha
- Controle de permissões por perfil
- Paginação da listagem de solicitações
- Testes automatizados com maior cobertura
- Documentação da API com OpenAPI/Swagger
- Logs estruturados
- Pipeline de CI/CD
- Containerização completa da aplicação
- Deploy em ambiente cloud

O cadastro de usuários pela interface, por exemplo, não foi implementado por não fazer parte dos requisitos solicitados. Para o cenário atual, os usuários de teste podem ser cadastrados diretamente no banco de dados.

## Objetivo do projeto

O projeto foi desenvolvido com foco em demonstrar conhecimentos de desenvolvimento full stack, incluindo:

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
