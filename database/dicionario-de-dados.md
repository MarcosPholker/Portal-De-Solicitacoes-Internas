# Dicionário de Dados

## Banco de dados

**Nome:** portal_solicitacoes

---

## Tabela: users

Armazena os usuários cadastrados no sistema.

| Campo | Tipo | Nulo | Chave | Descrição |
|---|---|---|---|---|
| id | UUID | Não | PK | Identificador único do usuário |
| email | VARCHAR(255) | Não | UNIQUE | E-mail utilizado pelo usuário |
| username | VARCHAR(255) | Não | - | Nome do usuário |
| password | VARCHAR(255) | Não | - | Senha armazenada de forma protegida |

---

## Tabela: requests

Armazena as solicitações internas cadastradas no sistema.

| Campo | Tipo | Nulo | Chave | Descrição |
|---|---|---|---|---|
| id | UUID | Não | PK | Identificador único da solicitação |
| title | VARCHAR(255) | Sim | - | Título da solicitação |
| description | VARCHAR(255) | Sim | - | Descrição da solicitação |
| internal_request_category | VARCHAR(255) | Sim | - | Categoria da solicitação |
| creation_date | TIMESTAMP | Sim | - | Data e hora de criação |
| internal_request_status | VARCHAR(255) | Sim | - | Status atual da solicitação |
| user_id | UUID | Sim | FK | Usuário responsável pela solicitação |

---

## Categorias

A aplicação possui as seguintes categorias:

- TI
- RH
- SALES
- FINANCIAL
- INFRASTRUCTURE

---

## Status

A aplicação possui os seguintes status:

- OPEN
- IN_PROGRESS
- COMPLETED

---

## Relacionamentos

A tabela `requests` possui uma chave estrangeira `user_id`
que referencia a tabela `users`.

Um usuário pode possuir várias solicitações.

Relacionamento:

users (1) ──────── (N) requests