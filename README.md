# Planner API

[![CI](https://github.com/AqueleGabrielLa/planner-api/actions/workflows/ci.yml/badge.svg?branch=dev)](https://github.com/AqueleGabrielLa/planner-api/actions/workflows/ci.yml)

API REST de gerenciamento de tarefas com autenticação JWT, multiusuário e persistência em PostgreSQL. Feito pra estudar Spring: security, JPA, validação, Flyway, testes e Docker.

---

## Sumário

- [Funcionalidades](#funcionalidades)
- [Stack](#stack)
- [Como rodar](#como-rodar)
  - [Com Docker (recomendado)](#com-docker-recomendado)
  - [Sem Docker (local)](#sem-docker-local)
  - [Testes](#testes)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [Endpoints](#endpoints)
- [Exemplos de uso](#exemplos-de-uso)
- [Tratamento de erros](#tratamento-de-erros)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Modelo de dados](#modelo-de-dados)

---

## Funcionalidades

- Cadastro e login com **JWT** (senha criptografada com BCrypt)
- **CRUD completo** de tarefas, isoladas por usuário autenticado
- **Validação** de entrada com Bean Validation (incluindo PATCH parcial)
- **Tratamento global de erros** com respostas padronizadas
- **Flyway** para versionamento do schema
- **Swagger UI** para explorar a API
- **Testes automatizados** (JUnit 5 + Mockito + H2 em memória)
- **CORS** configurável para consumir a API de um frontend
- **Docker Compose** com PostgreSQL e healthcheck

---

## Stack

| Camada | Tecnologia | Versão |
|---|---|---|
| Linguagem | Java | 21 |
| Framework | Spring Boot | 4.0.6 |
| Web | Spring MVC / Spring Web | (Spring Boot) |
| Segurança | Spring Security + JJWT | 0.12.6 |
| Persistência | Spring Data JPA + Hibernate | 7.2.x |
| Banco | PostgreSQL | 16 |
| Migrations | Flyway (+ `flyway-database-postgresql`) | (Spring Boot) |
| Validação | Bean Validation (Hibernate Validator) | (Spring Boot) |
| Documentação | springdoc-openapi (Swagger UI) | 2.8.8 |
| Build | Maven (wrapper incluído) | — |
| Utilitário | Lombok | (Spring Boot) |
| Testes | JUnit 5, Mockito, AssertJ, H2 | (Spring Boot) |
| Containerização | Docker + Docker Compose | — |

---

## Como rodar

### Com Docker (recomendado)

**Pré-requisito:** Docker.

```bash
git clone https://github.com/AqueleGabrielLa/planner-api.git
cd planner-api
docker compose up --build
```

O compose sobe o PostgreSQL na porta `5433` e a API na `8080`. A API espera o banco ficar pronto e o Flyway cria as tabelas na primeira subida. O `Dockerfile` é multi-stage: compila com Maven 21 e a imagem final contém só o JRE e o jar.

- API: **http://localhost:8080**
- Swagger UI: **http://localhost:8080/swagger-ui/index.html**
- OpenAPI JSON: **http://localhost:8080/v3/api-docs**

Para usar seu próprio secret JWT, defina a variável antes do comando:

```bash
JWT_SECRET="seu-secret-com-32-caracteres-ou-mais" docker compose up --build
```

> Sem `JWT_SECRET`, o compose usa um secret de teste.

Pra apagar os dados e começar de novo:

```bash
docker compose down -v
```

### Sem Docker (local)

**Pré-requisitos:** Java 21, Maven (ou use o `./mvnw`) e PostgreSQL 16 rodando na porta `5433` com o banco `plannerdb`.

1. Crie o banco e o usuário no PostgreSQL:

```sql
CREATE USER planner WITH PASSWORD 'planner123';
CREATE DATABASE plannerdb OWNER planner;
```

2. Copie o arquivo de configuração de exemplo e ajuste os dados da sua conexão:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

3. Defina o secret do JWT e suba:

```bash
export JWT_SECRET="seu-secret-com-32-caracteres-ou-mais"
./mvnw spring-boot:run
```

### Testes

```bash
./mvnw test
```

Rodam com H2 em memória, sem precisar de Postgres. Cobrem o contexto Spring, o `TaskService` e o cadastro de e-mail duplicado.

---

## Variáveis de ambiente

| Variável | Obrigatória | Padrão | Descrição |
|---|---|---|---|
| `JWT_SECRET` | Sim em produção | valor demo no compose | Secret HMAC do JWT (mínimo 32 caracteres) |
| `SPRING_DATASOURCE_URL` | Não | `jdbc:postgresql://localhost:5433/plannerdb` | URL JDBC do banco |
| `SPRING_DATASOURCE_USERNAME` | Não | `planner` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Não | `planner123` | Senha do banco |
| `CORS_ALLOWED_ORIGINS` | Não | `http://localhost:3000,http://localhost:5173` | Origens permitidas (separadas por vírgula) |

O Spring resolve automaticamente essas variáveis para as propriedades correspondentes (relaxed binding).

---

## Endpoints

Todas as rotas exigem o header `Authorization: Bearer <token>`, **exceto** `/auth/**` e a documentação.

| Método | Rota | Descrição | Auth | Sucesso |
|---|---|---|---|---|
| `POST` | `/auth/register` | Cadastra usuário e devolve token | Não | `200` |
| `POST` | `/auth/login` | Autentica e devolve token | Não | `200` |
| `GET` | `/task/` | Lista as tarefas do usuário | Sim | `200` |
| `GET` | `/task/{id}` | Busca uma tarefa por ID | Sim | `200` |
| `POST` | `/task/` | Cria uma tarefa | Sim | `200` |
| `PATCH` | `/task/{id}` | Atualiza campos enviados | Sim | `200` |
| `DELETE` | `/task/{id}` | Remove uma tarefa | Sim | `204` |
| `GET` | `/users/{id}` | Dados do próprio usuário autenticado | Sim | `200` |

**Corpos das requisições**

`POST /auth/register`

```json
{
  "name": "Seu Nome",
  "email": "usuario@email.com",
  "password": "123456"
}
```

`POST /auth/login`

```json
{
  "email": "usuario@email.com",
  "password": "123456"
}
```

`POST /task/`

```json
{
  "title": "Estudar matemática",
  "description": "Revisar somatório"
}
```

`PATCH /task/{id}` — todos os campos são opcionais; `title` não pode ser vazio.

```json
{
  "completed": true
}
```

**Resposta de autenticação**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Resposta de tarefa**

```json
{
  "id": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
  "title": "Estudar matemática",
  "description": "Revisar somatório",
  "completed": false,
  "createdAt": "2026-11-24T13:00:00"
}
```

O token JWT expira em **24 horas**.

---

## Exemplos de uso

Cadastro:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Seu Nome","email":"usuario@email.com","password":"123456"}'
```

Login (guarde o token retornado):

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"usuario@email.com","password":"123456"}'
```

Criar tarefa:

```bash
curl -X POST http://localhost:8080/task/ \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{"title":"Estudar matemática","description":"Revisar somatório"}'
```

Listar tarefas:

```bash
curl http://localhost:8080/task/ \
  -H "Authorization: Bearer SEU_TOKEN"
```

Concluir tarefa:

```bash
curl -X PATCH http://localhost:8080/task/ID_DA_TASK \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{"completed":true}'
```

---

## Tratamento de erros

Erros são padronizados:

```json
{
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Task com id ... não encontrada",
  "timestamp": "2026-11-24T13:00:00"
}
```

| Status | `error` | Quando acontece |
|---|---|---|
| `400` | `VALIDATION_ERROR` | Campo inválido (ex.: e-mail mal formatado, título vazio) |
| `400` | `BAD_REQUEST` | JSON malformado ou ID com formato inválido |
| `401` | `UNAUTHORIZED` | E-mail ou senha inválidos no login |
| `401` | — | Sem token ou token inválido/expirado |
| `404` | `NOT_FOUND` | Tarefa inexistente/de outro usuário ou tentativa de ler outro usuário |
| `409` | `CONFLICT` | E-mail já cadastrado |
| `500` | `INTERNAL_ERROR` | Erro inesperado (registrado no log do servidor) |

---

## Estrutura do projeto

```
src/
├── main/
│   ├── java/com/gabriel/plannerapi/
│   │   ├── auth/          # cadastro, login e DTOs de autenticação
│   │   ├── exception/     # exceções e handler global
│   │   ├── security/      # JWT, filtro, config de segurança e Swagger
│   │   ├── task/          # entidade, service, controller e DTOs de tarefa
│   │   ├── user/          # entidade, service, controller e DTOs de usuário
│   │   └── PlannerApiApplication.java
│   └── resources/
│       ├── db/migration/  # V1 (user_account) e V2 (task)
│       └── application.properties.example
└── test/
    ├── java/...           # testes de service, auth e carga do contexto
    └── resources/application.properties  # H2 em memória
```

---

## Modelo de dados

- **`user_account`** — `id` (UUID), `name`, `email` (único), `password` (BCrypt), `created_at`
- **`task`** — `id` (UUID), `title`, `description`, `completed`, `user_id` (FK), `created_at`

Relacionamento: **um usuário tem muitas tarefas** (`@ManyToOne` em `Task`). O schema é versionado pelo Flyway e validado pelo Hibernate (`ddl-auto=validate`).
