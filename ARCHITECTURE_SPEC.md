# SYSTEM ARCHITECTURE & BACKEND SPEC: "SCRIBBLEFIT"

## 1. NATIVE LOCAL STORAGE (The Source of Truth)

**Implementation:** `Room` (Android) / `SwiftData` or `SQLite.swift` (iOS).

* **Table: `Sync_Queue`**
    * `id` (UUID, PK), `raw_text` (String), `status` (Enum: PENDING, PROCESSING, FAILED, COMPLETED),
      `created_at` (Timestamp).
* **Table: `Workout_Logs` & `Sets`**
    * `Workout_Logs`: `id`, `date`, `location`, `total_volume`.
    * `Sets`: `id`, `workout_id` (FK), `exercise_id` (FK), `weight`, `reps`, `rpe`, `notes`.
* **Table: `Exercise_Dictionary`** (Synced from API)
    * `id` (UUID), `canonical_name` (String), `muscle_group` (String), `aliases` (JSON Array).
* **Table: `System_Config` & `Insights_Cache`**
    * Stores prompt versions, user settings, and cached LLM analytical summaries to prevent
      redundant API calls.

## 2. THE OFFLINE-FIRST PARSER ENGINE

**Implementation:** `WorkManager` (Android) / `BGTaskScheduler` & `URLSession` background
configurations (iOS).

* **Workflow:**
    1. User enters text. App inserts row into `Sync_Queue` with status `PENDING`.
    2. Background worker detects unparsed rows and checks network state.
    3. If online, worker fetches the payload, injects the local `System_Config` prompt, and calls
       the API Proxy.
    4. On success: Parse JSON, insert into `Workout_Logs`/`Sets`, update queue to `COMPLETED`.
    5. On fail/hallucination: Mark `FAILED`, optionally alert telemetry.

## 3. BACKEND ORCHESTRATION (Edge API)

**Implementation:** Ktor Edge Functions (Supabase / Cloudflare Workers).

* **Endpoint: `GET /api/sync/metadata`**
    * Returns the latest System Prompt hash and Exercise Dictionary version. The native app calls
      this on boot to keep local AI context fresh.
* **Endpoint: `GET /api/sync/exercises`**
    * Returns the structured JSON array of canonical exercises and slang aliases for local SQLite
      seeding.
* **Endpoint: `POST /api/parse/proxy`**
    * **Auth:** Validates app-level JWT or user subscription tier.
    * **Action:** Forwards the raw gym text to the LLM (OpenAI/Anthropic) using strict JSON-schema
      enforcement, returning the sanitized payload to the mobile client.
* **Endpoint: `POST /api/telemetry/errors`**
    * Catches failed parses or hallucinated JSON from the mobile clients to improve the global
      prompt.