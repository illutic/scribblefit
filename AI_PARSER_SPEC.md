# AI PARSER SPECIFICATION: SCRIBBLEFIT

## 1. KEY MANAGEMENT & MODES

ScribbleFit supports two operational modes for AI parsing:

### A. Bring Your Own Key (BYOK)
* **Description:** The user provides their own API key (OpenAI or Anthropic).
* **Storage:** 
    * **Android:** `EncryptedSharedPreferences` via `security-crypto` library.
    * **iOS:** `Keychain Services` with `kSecClassGenericPassword`.
* **Flow:** 
    1. User enters key in Settings.
    2. Key is stored securely.
    3. Background worker detects key and calls the AI provider's API directly from the device.

### B. Managed Service (Paid Subscription)
* **Description:** ScribbleFit provides the AI parsing via a backend proxy.
* **Flow:**
    1. App detects no local API key but a valid subscription token.
    2. Background worker calls `POST /api/parse/proxy` with the ScribbleFit JWT.
    3. Backend uses internal ScribbleFit keys to process the request.

---

## 2. PROVIDER ABSTRACTION (ENGINE)

Both apps must implement a `LLMEngine` interface to allow switching providers. The system prompt is injected into the implementation at initialization.

### Interface: `LLMEngine`
* `parseWorkout(rawText: String): Result<ParsedWorkout>`

### Supported Engines:
1. **OpenAI Engine:** Uses `gpt-4o-mini` or `gpt-4o` with `response_format: { type: "json_object" }`.
2. **Anthropic Engine:** Uses `claude-3-5-sonnet` with pre-filling or tool-use for JSON.
3. **Gemini Engine:** Uses `gemini-1.5-flash` or `gemini-1.5-pro` with `response_mime_type: "application/json"`.
4. **Local AI Engine:** Uses on-device models (Gemini Nano on Android via AICore, Apple Intelligence on iOS via FoundationModels).
5. **ScribbleFit Proxy Engine:** Wraps the network call to our Ktor backend.

---

## 3. DATA SCHEMA (JSON)

The AI must return a structure that maps directly to the `Workout_Logs` and `Sets` tables.

```json
{
  "date": "ISO8601 String",
  "location": "String?",
  "exercises": [
    {
      "canonical_name": "Bench Press",
      "sets": [
        {
          "weight": 135.0,
          "reps": 5,
          "rpe": 8.5,
          "notes": "Felt heavy"
        }
      ]
    }
  ]
}
```

---

## 4. BACKGROUND PROCESSING LOGIC

The `Sync_Queue` processor follows this priority:

1. **Check Local Settings:** Is an `api_key` present in secure storage?
2. **Yes:** Instantiate local `OpenAIEngine` or `AnthropicEngine` and execute.
3. **No:** Is `subscription_active` true?
4. **Yes:** Instantiate `ScribbleFitProxyEngine` and execute.
5. **No:** Mark `Sync_Queue` item as `FAILED` with "Authorization Required".

---

## 5. SECURITY REQUIREMENTS

* **No Logging:** API keys must never be printed to Logcat/Xcode console or captured in crash reports.
* **Network Pinning:** (Optional) Use SSL pinning for the ScribbleFit Proxy.
* **Key Revocation:** Provide a "Clear Key" button in settings that wipes the secure storage.
