# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

myb2bspace-services is a Spring Boot 3.4.0 application built with Java 21 and Gradle. It's a B2B organizer service that processes emails, manages documents, timesheets, and provides REST APIs for various business operations. The application uses MongoDB for persistence and Spring Integration for mail processing.

## Build and Run Commands

### Building
```bash
# Build the project
./gradlew build

# Build Docker image (requires Java 21)
./gradlew bootBuildImage

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "pro.b2borganizer.services.documents.control.MailContentParserTest"
```

### Running Locally
The application expects MongoDB running on localhost:27017 (default configuration in `src/main/resources/application.properties`).

```bash
# Run the application locally
./gradlew bootRun
```

### Docker Deployment
```bash
# After building the image, deploy using Docker Compose
cd docker
# Create .env file with SPRING_APPLICATION_JSON configuration (see README.md)
docker compose up -d
```

The application runs on port 8080 by default.

## Architecture

### Layered Architecture Pattern
The codebase follows a consistent three-layer architecture pattern across all modules:

- **boundary/** - REST API endpoints (controllers), named `*Resource.java`
- **control/** - Business logic, services, repositories, and configuration
- **entity/** - Domain models, DTOs, events, and exceptions

### Module Structure
The application is organized into functional modules under `pro.b2borganizer.services`:

- **mails** - Email processing using Spring Integration
  - IMAP integration for receiving emails (`MailFlowConfiguration`)
  - Email parsing (`MimeMessageParser`)
  - Mail sending and gateway (`MailGateway`, `MailSendingHandler`)
  - Stores mail messages in MongoDB

- **documents** - Document management and parsing
  - Handles PDF, DOC, DOCX, XLS, XLSX, TXT files
  - Parses document content from emails
  - AI-powered document categorization using Spring AI + Ollama
  - Embedding-based similarity search for automatic category assignment

- **users** - Authentication and authorization
  - JWT-based authentication (`JwtTokenProvider`, `JwtTokenFilter`)
  - API token authentication (`APITokenTokenFilter`)
  - Spring Security configuration (`SecurityConfiguration`)
  - BCrypt password encoding

- **files** - File storage and management

- **templates** - Template management for document generation

- **timesheets** - Work log tracking (`WorkLog`, `WorkLogsResource`)

- **reports** - Reporting functionality

- **tokens** - Token management

- **errors** - Error handling

- **common** - Shared utilities
  - `SimpleRestProviderRepository` - Generic MongoDB repository with filtering/sorting
  - `SimpleRestProviderFilter` - Query building for REST endpoints
  - `SimpleRestProviderQueryParser` - Parses REST query parameters into MongoDB queries

- **migrations** - Database migrations using Mongock
  - Migration classes follow naming pattern `ChangeSet_DD_MM_YYYY_NNN.java`
  - Configured in application.properties: `mongock.migration-scan-package=pro.b2borganizer.services.migrations`

### Key Technologies
- **Spring Boot 3.4.0** with Spring Data MongoDB, Spring Integration, Spring Security
- **Spring AI 1.0.0-M4** - AI integration with Ollama for embeddings and LLM
- **MongoDB** - Main data store
- **Mongock** - Database migration tool (version 5.4.0)
- **Lombok** - Reduces boilerplate code
- **MapStruct** - Object mapping (version 1.6.0)
- **JWT** (io.jsonwebtoken:jjwt:0.12.3) - Authentication tokens
- **Apache PDFBox 3.0.2** - PDF processing
- **Spring Integration Mail** - Email processing with IMAP/SMTP support

### Spring Integration Mail Flow
The mail processing uses Spring Integration DSL pattern:
- `inboundMailFlow()` - IMAP poller (5 second intervals) reads from INBOX, publishes to "imapChannel"
- `outboundMailFlow()` - Processes outgoing mail through "mailChannel" to "mailOutputChannel"
- `MailReceivingHandler` and `MailSendingHandler` handle the actual mail processing logic

### AI Document Categorization
The application uses Spring AI with Ollama for automatic document categorization based on historical data:

**Architecture:**
- **Embeddings:** Text content is extracted from documents (PDF, TXT) and converted to vector embeddings using Ollama's `nomic-embed-text` model
- **Knowledge Base:** Embeddings from all categorized documents are stored in MongoDB (`DocumentEmbedding` collection)
- **Similarity Search:** New documents are categorized by finding the K most similar documents using cosine similarity
- **Voting Mechanism:** Category is determined by majority vote from top-5 similar documents with 60% confidence threshold

**Key Components:**
- `DocumentTextExtractor` - Extracts text from PDF/TXT files using PDFBox
- `DocumentEmbeddingService` - Generates and manages embeddings using Spring AI's `EmbeddingModel`
- `DocumentCategorizationService` - Implements similarity search and voting logic
- `DocumentEmbedding` entity - Stores embeddings with reference to original document and category
- `ManagedDocument.aiConfidence` - Stores confidence score (0.0-1.0) for AI categorizations
- `RequiredDocumentSelectionType.AI` - Marks documents categorized by AI

**REST Endpoints:**
- `POST /managed-documents-categorizations` - Categorize single document (with documentId) or all uncategorized documents (empty body)
- `POST /managed-documents-embeddings` - Rebuild embeddings knowledge base from all categorized documents

**Workflow:**
1. Manually categorize initial set of documents to build training data
2. Run `POST /managed-documents-embeddings` to build the knowledge base
3. Run `POST /managed-documents-categorizations` to auto-categorize new documents
4. Review AI-categorized documents (check `aiConfidence` score and `requiredDocumentSelectionType`)

**Prerequisites:**
- Ollama must be running locally on `http://localhost:11434` (configurable via `spring.ai.ollama.base-url`)
- Ollama must have `nomic-embed-text` model installed: `ollama pull nomic-embed-text`

### Security
- JWT-based authentication for user sessions (configured via `jwt.secret` and `jwt.expirationInMillis`)
- API token authentication for service-to-service communication
- Two authentication filters: `APITokenTokenFilter` → `JwtTokenFilter` (order matters)
- CORS enabled with wildcard origins (all origins allowed)
- Stateless session management
- Only `/authentications` endpoint is public; all others require authentication

### REST API Patterns
- Resources use Spring Data REST conventions
- Generic filtering and sorting via `SimpleRestProviderRepository`
- MongoDB Query DSL for complex queries
- Jackson with JsonNullable support for PATCH operations

## Configuration

### Application Properties
Main configuration is in `src/main/resources/application.properties`:
- MongoDB connection settings
- Mail receiver (IMAP) configuration: `pro.b2organizer.mail.receiver.*`
- Mail sender (SMTP) configuration: `spring.mail.*`
- JWT settings: `jwt.secret`, `jwt.expirationInMillis`
- Error notification email: `pro.b2borganizer.mail.errorDestinationMailAddress`
- Allowed document extensions: `pro.b2borganizer.allowedManagedDocumentExtensions`
- Ollama AI configuration:
  - `spring.ai.ollama.base-url` - Ollama server URL (default: http://localhost:11434)
  - `spring.ai.ollama.embedding.options.model` - Embedding model (default: nomic-embed-text)
  - `spring.ai.ollama.chat.options.model` - Chat model (default: llama3.2)

For Docker deployment, configuration is provided via `SPRING_APPLICATION_JSON` environment variable (see README.md for complete structure).

### Profiles
Default profile is `local` (`spring.profiles.active=local`).

## Development Guidelines

### Adding New Features
1. Follow the three-layer pattern: boundary/control/entity
2. Use Lombok annotations (@RequiredArgsConstructor, @Slf4j, @Data) to reduce boilerplate
3. Use MapStruct for entity-to-DTO mapping
4. For REST endpoints, extend from Spring Data REST patterns where possible
5. Use `SimpleRestProviderRepository` for generic CRUD operations with filtering

### Database Migrations
Create new migration classes in `services/migrations/` following the naming pattern and Mongock conventions. Migrations run automatically on application startup.

### Mail Processing
Email handling logic should be added to the Spring Integration flow. New handlers should follow the pattern of `MailReceivingHandler` and `MailSendingHandler`.

### Authentication
- New endpoints require authentication by default (see `SecurityConfiguration:66`)
- To make an endpoint public, add it to `.requestMatchers()` in `SecurityConfiguration`
- JWT tokens expire based on `jwt.expirationInMillis` (default 900000ms = 15 minutes)