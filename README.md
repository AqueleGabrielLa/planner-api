# Planner API

API REST de gerenciamento de tarefas desenvolvida com Spring Boot 

O projeto foi criado com o objetivo de estudar e aplicar boas práticas do ecossistema Spring, incluindo autenticação, persistência de dados, validação, testes e conteinerização. Novas funcionalidades serão adicionadas ao longo do tempo

## Conteúdo

- Autenticação JWT (registro e login)
- CRUD completo de tasks por usuário autenticado
- Tratamento de erros global
- Validações com Bean Validation
- Testes unitários com JUnit 5 + Mockito
- Documentação com Swagger UI

## Stack
- Java 21 + Spring Boot
- Spring Security + JWT
- Spring Data JPA + PostgreSQL
- Flyway para migrations
- Docker

## Como rodar
**Pré-requisito:** Docker instalado.

```bash
git clone https://github.com/AqueleGabrielLa/planner-api.git
cd planner-api
docker compose up --build
```

A API sobe em `http://localhost:8080`
Swagger disponível em `http://localhost:8080/swagger-ui/index.html`

## Variáveis de ambiente

Copie o arquivo de exemplo e ajuste o necessário

```bash
cp application.properties.example application.properties
```


