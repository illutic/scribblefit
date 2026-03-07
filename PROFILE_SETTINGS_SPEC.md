# PROFILE & SETTINGS SPECIFICATION: SCRIBBLEFIT

## 1. OVERVIEW
The Profile & Settings screen serves as the user's personal hub and the application's configuration center. It provides high-level lifetime statistics and granular control over the AI parsing engine, preferences, and data management.

---

## 2. UI/UX REQUIREMENTS

### A. Profile Header
*   **Avatar:** A large, circular placeholder with a soft gray background (`#F7F7F8`) and the user's initial in rich black (`#101010`).
*   **Identity:** User's name in bold system font, followed by a mid-gray subtitle ("Member since [Month Year]").

### B. Lifetime Statistics Grid
*   **Layout:** A 2-column grid of borderless cards.
*   **Styling:** Soft gray background (`#F7F7F8`), rounded corners (16dp).
*   **Key Metrics:**
    *   `Total Workouts`: Count of all committed `WorkoutHistory` entries.
    *   `Lifetime Volume`: Sum of all weight × reps across all logs.
    *   `PRs Hit`: Count of identified personal records.
    *   `Active Streak`: Days since the last workout if within a 3-day window.

### C. Settings Configuration List
A vertically scrolling list grouped into logical sections, separated by faint dividers (`#E5E5EA`).

#### **Section 1: AI Engine (Critical)**
*   **Parsing Mode:** A toggle/switch between "ScribbleFit Cloud" (Managed) and "Personal API Key" (BYOK).
*   **API Key Input:** A masked text field visible ONLY when "Personal API Key" is selected. Includes a "Test Connection" action.
*   **Provider Selection:** A segmented control or pill-based selector to choose between "OpenAI" and "Anthropic".
*   **Model Selection:** (Optional/Advanced) Dropdown to select specific models (e.g., GPT-4o, Claude 3.5 Sonnet).

#### **Section 2: Preferences**
*   **Weight Units:** Toggle between "lbs" and "kg".
*   **Appearance:** Toggle between "Light", "Dark", and "System Default".

#### **Section 3: Data & Storage**
*   **Export Ledger:** Action to generate a CSV/JSON export of all workout data.
*   **Sync Status:** Glanceable indicator of the `Sync_Queue` health.

#### **Section 4: Danger Zone**
*   **Clear All Data:** A prominent red text action (`#FF3B30`) with a confirmation dialog to wipe local Room/SwiftData stores.

---

## 3. DATA MODELS

### `UserStats`
```kotlin
data class UserStats(
    val totalWorkouts: Int,
    val lifetimeVolume: Double,
    val prCount: Int,
    val joinDate: Long
)
```

### `AppSettings`
```kotlin
data class AppSettings(
    val parsingMode: ParsingMode,
    val aiApiKey: String?,
    val aiProvider: LLMProvider,
    val weightUnit: WeightUnit,
    val themePreference: ThemePreference
)

enum class ParsingMode { CLOUD, PERSONAL }
enum class WeightUnit { LBS, KG }
enum class ThemePreference { LIGHT, DARK, SYSTEM }
```

---

## 4. ARCHITECTURAL COMPONENTS

### New Repositories
1.  **`UserRepository`**: 
    *   Calculates and provides live updates for `UserStats` by aggregating `LedgerRepository` data.
    *   `fun getUserStats(): Flow<UserStats>`
2.  **`SettingsRepository`**:
    *   Manages the persistence of `AppSettings` in `System_Config` (Room) or `UserDefaults` (iOS).
    *   `fun getSettings(): Flow<AppSettings>`
    *   `suspend fun updateSettings(settings: AppSettings)`

### New Use Cases
1.  **`ValidateApiKeyUseCase`**: 
    *   Logic: Performs a "ping" request to the selected AI provider using the entered key to verify its validity.
2.  **`WipeUserDatabaseUseCase`**:
    *   Logic: Coordinates the clearing of all tables across all modules (`Canvas_Feed`, `Workout_Logs`, `Sync_Queue`, etc.).
3.  **`ExportWorkoutDataUseCase`**:
    *   Logic: Aggregates `WorkoutHistory` into a shareable file format.

---

## 5. TECHNICAL CONSTRAINTS
*   **Security:** API keys MUST be stored in the **SecureKeyStorage** (EncryptedSharedPreferences on Android / Keychain on iOS) and never exposed in plain text logs.
*   **Reactivity:** The `AI Engine` must reactively update its configuration as soon as the `SettingsRepository` emits a change, without requiring an app restart.
