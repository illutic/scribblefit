# ScribbleFit API

Orchestration layer for metadata sync, exercise dictionary seeding, and AI-powered text parsing.

## 🚀 Tech Stack
- **Framework:** Ktor
- **Platform:** Supabase or Cloudflare Workers (via Kotlin/Wasm or GraalVM)
- **AI Integration:** OpenAI / Anthropic for structured JSON parsing

## 🛣️ Key Endpoints
- `GET /api/sync/metadata`: Latest system prompt hashes and dictionary versions.
- `GET /api/sync/exercises`: Structured JSON of canonical exercises and aliases.
- `POST /api/parse/proxy`: Secure LLM proxy with strict JSON-schema enforcement.
- `POST /api/telemetry/errors`: Reporting for failed parses and AI hallucinations.

## 🛠️ Development Conventions
- **Strict JSON Enforcement:** All responses to mobile clients must adhere to strict JSON-schema enforcement to prevent parsing errors in the native apps.
- **Edge Performance:** Keep function execution times low. Avoid heavy computations; delegate complex AI parsing to specialized LLM proxies.

## 🛠️ Development Guidelines
- **Framework:** **Ktor** (Kotlin). Use the latest stable version.
- **Architecture:** Layered architecture with a clear separation between **Routing**, **Services** (Business Logic), and **Data Access Objects (DAOs)**.
- **Concurrency:** Leverage **Kotlin Coroutines** for non-blocking I/O operations.
- **Security:**
    - Validate all incoming requests using **JWT** or app-level tokens.
    - Never expose raw LLM API keys; use environment variables or a secure vault.
- **Persistence:** Use **Exposed** or a similar Kotlin-friendly ORM for database interactions if needed.
- **Testing:**
    - **Unit Tests:** For services and logic.
    - **Integration Tests:** Use Ktor's `testApplication` to verify API endpoints and response formats.
- **Documentation:** Maintain up-to-date API documentation (e.g., OpenAPI/Swagger) for the mobile clients.

## 🛠️ Development
- **Setup:** 
  - Ensure you have **JDK 17+** installed.
  - Configure your local environment by creating a `.env` file from `.env.example`.
- **Local Dev:** `./gradlew run` (Starts the Ktor server in development mode).
- **Build:** `./gradlew build`
- **Testing:** `./gradlew test`
- **Linting:** `./gradlew detekt`
- **Deployment:** Instructions for deployment depend on the target platform (e.g., `./gradlew shadowJar` for generic JVM, or platform-specific Gradle tasks).
