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

O compose já injeta um secret de demonstração no container. Para usar o seu próprio:

```bash
JWT_SECRET="seu-secret-com-32-caracteres-ou-mais" docker compose up --build
```

Para rodar a API fora do Docker (IDE ou `./mvnw spring-boot:run`), copie o exemplo e defina o secret:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
export JWT_SECRET="seu-secret-com-32-caracteres-ou-mais"
./mvnw spring-boot:run
```

O `application.properties` local não é versionado e guarda apenas placeholders — o secret nunca entra no jar.


