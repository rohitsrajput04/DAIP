# DAIP — DB AI Decision Intelligence Platform

Enterprise-grade DB AI Decision Intelligence Platform (DAIP) for global investment banking. Modular architecture supports pluggable business domains (ESG, AML, Risk, Compliance, Treasury, etc.) without core changes.

## Architecture

```
daip-platform/
├── backend/          # Java 21 + Spring Boot 3 (layered architecture)
├── frontend/         # React + Bootstrap + Axios
├── k8s/              # Kubernetes manifests
└── docker-compose.yml
```

### Backend Layers
- **Controller** — REST API endpoints
- **Service** — Business logic
- **Repository** — JPA data access
- **Entity / DTO / Mapper** — Domain model & API contracts
- **Security** — JWT authentication
- **AI** — LangChain4j-ready chat provider (Phase 2+)
- **Config / Exception / Utils**

## Phase 2 (Implemented)

- PDF, Word (.doc/.docx), Excel (.xls/.xlsx), and text extraction
- Text chunking with configurable overlap
- OpenAI embeddings via LangChain4j → pgvector storage
- Semantic search (vector) with keyword fallback when no API key
- RAG pipeline (`/api/v1/rag/query`) with source citations
- AI Chat integrated with RAG retrieval
- Automatic indexing on upload + manual reindex
- Sample AML policy document seeded on first startup

## Phase 1 (Implemented)

- JWT Authentication & Login
- Executive Dashboard with KPIs
- Sidebar Navigation
- AI Chat UI (rule-based, LangChain4j-ready)
- Document Upload (REST + UI)
- PostgreSQL + pgvector extension
- Swagger/OpenAPI documentation
- Docker Compose deployment
- Sample users & unit tests

## Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 21 + Maven (local dev)
- Node.js 20+ (local frontend dev)

### Run with Docker

```bash
cd daip-platform
cp .env.example .env
docker compose up --build
```

| Service   | URL                              |
|-----------|----------------------------------|
| Frontend  | http://localhost:3000            |
| Backend   | http://localhost:8080            |
| Swagger   | http://localhost:8080/swagger-ui.html |
| PostgreSQL| localhost:5432                   |

### Demo Accounts

| Username   | Password       | Role               |
|------------|----------------|--------------------|
| admin      | admin123       | ADMIN              |
| analyst    | analyst123     | ANALYST            |
| compliance | compliance123  | COMPLIANCE_OFFICER |

### Local Development

**Backend:**
```bash
cd backend
mvn spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm start
```

## API Endpoints

| Method | Endpoint                    | Description          | Auth |
|--------|-----------------------------|----------------------|------|
| POST   | /api/v1/auth/login          | Login & get JWT      | No   |
| GET    | /api/v1/dashboard           | Dashboard metrics    | Yes  |
| GET    | /api/v1/users/me            | Current user profile | Yes  |
| POST   | /api/v1/documents           | Upload document      | Yes  |
| GET    | /api/v1/documents           | List all documents   | Yes  |
| POST   | /api/v1/documents/{id}/reprocess | Reindex document       | Yes  |
| POST   | /api/v1/search              | Semantic search          | Yes  |
| GET    | /api/v1/search              | Semantic search (GET)    | Yes  |
| POST   | /api/v1/rag/query           | RAG question answering   | Yes  |
| POST   | /api/v1/chat                | Send chat message (RAG)  | Yes  |
| GET    | /api/v1/chat/sessions       | List chat sessions   | Yes  |

Full API docs: `http://localhost:8080/swagger-ui.html`

## Roadmap

| Phase | Feature                                      | Status      |
|-------|----------------------------------------------|-------------|
| 1     | Login, Dashboard, Chat, Documents, Docker    | ✅ Done     |
| 2     | RAG, Embeddings, pgvector, Semantic Search   | ✅ Done     |
| 3     | Multi-Agent Orchestration (LangGraph-ready)  | 🔜 Next     |
| 4     | Neo4j Knowledge Graph                        | Planned     |
| 5     | Explainable AI                               | Planned     |
| 6     | MCP Tool Layer                               | Planned     |
| 7     | DAIP extended features (Reports, Compare, Recommendations) | Planned    |

## Testing

```bash
cd backend && mvn test
cd frontend && npm test
```

## License

Proprietary — Internal use only.
