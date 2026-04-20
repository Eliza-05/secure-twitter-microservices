# CLAUDE.md — Mini-Twitter TDSE

## ⚠️ REGLA ABSOLUTA — COMMITS DE GIT

**NUNCA hagas un commit de forma automática o sin permiso explícito.**

Cuando termines una tarea o consideres que algo merece un commit:
1. Dime qué archivos cambiaste y por qué
2. Sugiere un nombre de commit en formato: `tipo(scope): descripción` (ejemplo: `feat(security): add AudienceValidator for Auth0 JWT`)
3. Espera. Yo decido si hacer el commit y yo lo ejecuto manualmente.

**Esto incluye:** `git add`, `git commit`, `git push`, `git stash` — cualquier operación de git que modifique el historial. Solo puedes ejecutar `git status`, `git diff` o `git log` para leerme el estado del repo.

---

## Contexto del proyecto

Taller grupal de la materia **Arquitecturas de Software y Sistemas Distribuidos (TDSE)** en la **Escuela Colombiana de Ingeniería Julio Garavito**. Se construye una mini-Twitter con dos fases: monolito Spring Boot y migración a microservicios serverless en AWS, todo asegurado con Auth0.

**Grupo de 3 personas. Plazo: 3 días.**

---

## Enunciado resumido

Construir una aplicación tipo Twitter simplificada donde usuarios autenticados puedan:
- Crear posts de máximo **140 caracteres**
- Ver un **stream público global** con todos los posts

### Fases obligatorias
1. **Monolito Spring Boot** con API REST + Swagger/OpenAPI + Auth0
2. **Migración a 3 microservicios** en AWS Lambda + API Gateway
3. **Frontend React** en S3 consumiendo la API con Auth0 SDK

### Entidades principales
- `User` — id, auth0Id, email, username, createdAt
- `Post` — id, content (max 140 chars), authorId, createdAt
- `Stream` — NO es una tabla; es el endpoint `GET /api/stream` que devuelve todos los posts ordenados por fecha

### Endpoints requeridos por el enunciado
| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| GET | `/api/stream` | ❌ Público | Stream global de posts |
| GET | `/api/posts` | ❌ Público | Alias del stream (paginable) |
| POST | `/api/posts` | ✅ JWT | Crear post (max 140 chars) |
| GET | `/api/me` | ✅ JWT | Info del usuario autenticado |

### Seguridad
- **Auth0 es obligatorio** (no Cognito, no otra cosa)
- Spring Boot configurado como **OAuth2 Resource Server**
- Frontend usa **Auth0 React SDK** (`@auth0/auth0-react`)
- JWT Bearer token en todos los endpoints protegidos
- Swagger debe documentar el esquema de seguridad JWT

### Entregables del enunciado
- Repositorio GitHub con todo el código
- README.md con: descripción, diagrama de arquitectura, setup local, reporte de tests, link al frontend en S3, link al Swagger
- Video de 5-8 minutos en Google Drive (NO commitear al repo)
- Swagger UI accesible en `/swagger-ui/index.html`

---

## Enunciado original del profesor

> Assignment - EXPERIMENTAL
> Building a Secure Twitter-like Application with Microservices and Auth0
>
> **Objective:** Design and implement a simplified Twitter-like application that allows authenticated users to create short posts (maximum 140 characters) in a single public stream/feed. The project must start as a Spring Boot monolith, evolve into serverless microservices on AWS, and be fully secured using Auth0.
>
> **Instructions:**
>
> Work in groups of maximum 3 students.
>
> **1. API Design and Monolith Development**
> Design a RESTful API and implement a Spring Boot monolithic application that allows authenticated users to create posts of up to 140 characters. All posts are stored and displayed in a single public stream (similar to a Twitter feed). Consider at least the following main entities: User, Post, Stream (a single global stream of posts).
>
> **2. API Documentation**
> Include a complete Swagger (OpenAPI) definition of your API in the Spring Boot monolith. The Swagger UI should be accessible (e.g., at /swagger-ui.html or /swagger-ui/index.html) and clearly document all endpoints, request/response models, parameters, and security requirements (JWT Bearer token).
>
> **3. Frontend Application**
> Develop a JavaScript-based web application (you may use vanilla JavaScript, React, Vue.js, or Angular) that consumes the backend API. The frontend should allow users to: Log in / Log out, Create new posts, View the public stream of posts. Deploy the frontend as a static website on Amazon S3 and ensure it is publicly accessible over the internet.
>
> **4. API Security (Mandatory)**
> Secure the entire RESTful API using Auth0 as the identity and authorization provider.
> - Create a Single Page Application (SPA) client in Auth0 for the frontend.
> - Define a dedicated API in Auth0 with a unique Audience for the backend.
> - Implement authentication and authorization using JWT access tokens issued by Auth0.
> - The frontend must integrate with Auth0 using the official Auth0 React SDK (strongly recommended) or Auth0 SPA JS SDK. It should handle login (redirect or popup), logout, silent token refresh, and secure token management.
> - The Spring Boot backend must be configured as an OAuth2 Resource Server. It should validate JWT tokens using Auth0's issuer URI (https://YOUR-DOMAIN.auth0.com/) and enforce the correct Audience.
> - Protect your endpoints as follows:
>   - Public endpoints (no authentication required): e.g. GET /api/posts or GET /api/stream — to read the public post stream.
>   - Protected endpoints (require valid JWT): e.g. POST /api/posts — only authenticated users can create posts.
>   - Add a protected endpoint to retrieve the current user's information: GET /api/me.
> - (Recommended) Define and use scopes such as read:posts, write:posts, and read:profile for granular authorization.
>
> **5. Testing**
> Thoroughly test the complete web application (secure frontend + protected backend).
>
> **6. Migration to Microservices**
> Refactor the monolith into at least three independent microservices. Suggested services: User Service / Authentication, Posts Service, Stream / Feed Service. Implement each microservice using AWS Lambda (serverless).
>
> **7. Deployment on AWS**
> Deploy all microservices on AWS Lambda (with API Gateway if needed). Ensure the frontend hosted on S3 can securely consume the microservices through Auth0-protected endpoints.
>
> **Deliverables** — Submit everything in a GitHub repository:
> - All source code (monolith, microservices, and frontend).
> - A comprehensive README.md that includes:
>   - Project description and final architecture overview.
>   - Architecture diagram (showing evolution from monolith to microservices + Auth0 security flow).
>   - Clear setup and local execution instructions.
>   - Report of tests performed.
>   - Link to the live frontend deployed on Amazon S3.
>   - Link to the Swagger UI of the monolith (if still deployed) or a screenshot/export of the OpenAPI specification.
> - A video demonstration (recommended 5–8 minutes) showing the fully working application: login with Auth0, creating posts, viewing the stream, retrieving user info via /api/me, and a brief explanation of the architecture and security configuration.
>
> **Important Notes:**
> - Using Auth0 for API security is mandatory.
> - The Swagger / OpenAPI documentation is mandatory for the monolith phase.
> - Do not commit sensitive credentials, Auth0 secrets, or API keys to GitHub. Use environment variables or AWS Secrets Manager where appropriate.
> - Focus on clean code, proper error handling, security best practices, and good user experience.

---

## Arquitectura objetivo

```
Usuario
  │
  ▼
[S3 — React frontend]
  │  Auth0 SDK (login/logout/token)
  ▼
[Auth0] ──────────────────────────────┐
  │ emite JWT                         │ valida JWT
  ▼                                   ▼
[API Gateway]               [Spring Boot monolito]
  │                          (Fase 1 — con Swagger)
  ├── Lambda: stream-service  → GET /api/stream
  ├── Lambda: post-service    → POST /api/posts   (Fase 2)
  └── Lambda: user-service    → GET /api/me
          │
          ▼
     [PostgreSQL — RDS o local]
```


## Stack técnico

| Capa | Tecnología |
|------|-----------|
| Backend monolito | Java 17, Spring Boot 3.x, Maven |
| Seguridad | Auth0, Spring Security OAuth2 Resource Server |
| Base de datos | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Documentación API | springdoc-openapi (Swagger UI) |
| Frontend | React, @auth0/auth0-react |
| Microservicios | AWS Lambda (Python 3.11 o Node 18) |
| API Gateway | AWS API Gateway HTTP API |
| Frontend hosting | Amazon S3 (static website) |
| Contenedores Docker | amazoncorretto:17 (si se necesita) |

---

## Convenciones del proyecto

- Package base: `edu.eci.tdse.minitwitter`
- Nombres en inglés para código, español para comentarios cuando sea necesario
- Nunca commitear: `.env`, `application-local.properties`, credenciales AWS, secrets de Auth0
- `.gitignore` debe incluir desde el inicio: `.env`, `.idea/`, `target/`, `*.jar`, `node_modules/`
- Variables de entorno para todos los secrets
- `LinkedHashMap` sobre `Map.of()` cuando el orden del JSON importa
- `StringBuilder` en loops, no concatenación de strings
- Validaciones en la capa de servicio, no en el controlador

