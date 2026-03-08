# PROFILE SPEC — ScribbleFit

> Covers settings, BYOK (bring your own key), model selection, and user stats.
> Depends on: `specs/CORE.md`, `specs/AI_SYNC.md` (for SystemConfig schema)

---

## 1. OVERVIEW

Profile has three repositories:
- **SettingsRepository**: reads/writes `SystemConfig` (AI provider, weight unit, theme, model)
- **UserRepository**: computes stats (total workouts, lifetime volume, join date) from `LedgerRepository`
- **ModelRepository**: fetches available model names from OpenAI/Gemini APIs using user's API key

---

## 2. DOMAIN LAYER

### File: `Profile/Domain/Repository/ProfileRepositories.swift` / `.kt`

```swift
// iOS
protocol UserRepository: Sendable {
    func getUserStats() async throws -> UserStats
}

protocol SettingsRepository: Sendable {
    func getSettings() async throws -> AppSettings
    func updateSettings(_ settings: AppSettings) async throws
    func clearAllData() async throws
}

protocol ModelRepository: Sendable {
    func fetchModels(for provider: LLMProvider, apiKey: String) async throws -> [String]
}
```

```kotlin
// Android
interface UserRepository {
    fun getUserStats(): Flow<UserStats>
}

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun updateSettings(settings: AppSettings)
    suspend fun clearAllData()
}

interface ModelRepository {
    suspend fun fetchModels(provider: LLMProvider, apiKey: String): List<String>
}
```

### File: `Profile/Domain/Models/ProfileModels.swift` / `ProfileModels.kt`

```swift
// iOS
struct UserStats: Sendable {
    let totalWorkouts: Int
    let lifetimeVolume: Double
    let prCount: Int          // placeholder, always 0
    let joinDate: Date
}

struct AppSettings: Sendable {
    var aiProvider: LLMProvider
    var weightUnit: WeightUnit
    var themePreference: ThemePreference
    var selectedModel: String?    // nil means "auto" / use default
}

enum WeightUnit: String, Codable, Sendable { case lbs, kg }
enum ThemePreference: String, Codable, Sendable { case light, dark, system }
// LLMProvider defined in Core/AI/LLMEngine.swift: openai, gemini, local
```

```kotlin
// Android adds ParsingMode
data class UserStats(
    val totalWorkouts: Int,
    val lifetimeVolume: Double,
    val prCount: Int,
    val joinDate: Long     // epoch ms
)

data class AppSettings(
    val parsingMode: ParsingMode = ParsingMode.CLOUD,
    val aiProvider: LLMProvider = LLMProvider.PROXY,
    val weightUnit: WeightUnit = WeightUnit.LBS,
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val selectedModel: String = ""
)

enum class ParsingMode { CLOUD, PERSONAL }
enum class WeightUnit { LBS, KG }
enum class ThemePreference { LIGHT, DARK, SYSTEM }
```

---

## 3. DATA LAYER

### `SettingsRepositoryImpl`

Reads/writes `SystemConfig` in the database. On update, **preserves** `promptVersion` and `promptText` from the existing config.

**`getSettings()` defaults:**
| Setting | Default |
|---------|---------|
| aiProvider | proxy |
| weightUnit | lbs |
| themePreference | system |
| selectedModel | nil / "" (auto) |
| parsingMode (Android) | cloud |

**`updateSettings()` logic:**
```swift
// iOS
func updateSettings(_ settings: AppSettings) async throws {
    let existing = await database.getConfig(id: "config")
    let config = SystemConfig(
        id: "config",
        promptVersion: existing?.promptVersion ?? "1.0.0",
        promptText: existing?.promptText ?? SystemConfig.defaultPrompt,
        exerciseVersion: existing?.exerciseVersion ?? "0.0.0",
        preferredLlmProvider: settings.aiProvider.rawValue,
        preferredModel: settings.selectedModel ?? "",
        weightUnit: settings.weightUnit.rawValue,
        themePreference: settings.themePreference.rawValue,
        updatedAt: Date()
    )
    await database.upsertConfig(config)
}
```

**`clearAllData()`** → calls `database.deleteAll()` (wipes all tables)

### `UserRepositoryImpl`

Derived from `LedgerRepository` — no separate database table.

```swift
// iOS
func getUserStats() async throws -> UserStats {
    let history = try await ledgerRepository.getWorkoutHistory()
    return UserStats(
        totalWorkouts: history.count,
        lifetimeVolume: history.reduce(0) { $0 + $1.totalVolume },
        prCount: 0,
        joinDate: history.min(by: { $0.date < $1.date })?.date ?? Date()
    )
}
```

### `ModelRepositoryImpl`

Fetches model names from provider APIs. No caching — always fetches fresh.

**OpenAI:**
- `GET https://api.openai.com/v1/models`
- Header: `Authorization: Bearer {apiKey}`
- Filter: model IDs starting with `gpt-`, `o1`, `o3`
- Return sorted list

**Gemini:**
- `GET https://generativelanguage.googleapis.com/v1beta/models?key={apiKey}`
- Filter: models that support `generateContent` method
- Return sorted model names (strip `models/` prefix)

**Other providers (proxy, local):** return `[]`

---

## 4. VIEW MODELS

### `SettingsViewModel`

```swift
// iOS
struct SettingsUiState: Sendable {
    var settings: AppSettings = AppSettings(aiProvider: .proxy, weightUnit: .lbs, themePreference: .system, selectedModel: nil)
    var availableModels: [String] = []
    var isLoadingModels: Bool = false
    var isSaving: Bool = false
    var apiKey: String = ""
    var showApiKeyField: Bool = false   // true when provider != proxy
}

@MainActor class SettingsViewModel: ObservableObject {
    @Published var uiState = SettingsUiState()

    func loadSettings() async
    func onProviderChanged(_ provider: LLMProvider) async
    func onModelSelected(_ model: String)
    func onApiKeySaved(_ key: String) async      // stores in SecureKeyStorage
    func onWeightUnitChanged(_ unit: WeightUnit) async
    func onThemeChanged(_ theme: ThemePreference) async
    func onClearDataTapped() async               // calls clearAllData()
    func fetchModels() async                     // calls ModelRepository
}
```

### `ProfileViewModel`

```swift
// iOS
struct ProfileUiState: Sendable {
    var userName: String = "George"
    var stats: UserStats? = nil
    var isLoading: Bool = true
}

@MainActor class ProfileViewModel: ObservableObject {
    @Published var uiState = ProfileUiState()

    func refreshStats() async
    func onSettingsClick()    // navigates to .settings
}
```

```kotlin
// Android
data class ProfileUiState(
    val userName: String = "George",
    val stats: UserStats? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(...) : ViewModel() {
    val uiState: StateFlow<ProfileUiState>
    fun onSettingsClick()
}
```

---

## 5. BYOK (BRING YOUR OWN KEY) FLOW

```
User selects provider (openai | gemini)
      │
      ▼
SettingsViewModel.onProviderChanged()
      │  shows API key input field
      │
      ▼
User enters API key → onApiKeySaved(key)
      │  SecureKeyStorage.saveApiKey(key)
      │  fetchModels() — calls ModelRepositoryImpl with key
      │
      ▼
User selects model from dropdown → onModelSelected(model)
      │  updateSettings(settings.copy(selectedModel: model))
      │
      ▼
DynamicLLMEngine reads provider + model from ConfigRepository
      │  GeminiAIEngine / OpenAIEngine reads key from SecureKeyStorage
```

**When provider = local**: No API key needed, no model selection.

---

## 6. SECURITY

- API key stored in **iOS Keychain** (`kSecClassGenericPassword`) / **Android EncryptedSharedPreferences**
- Key is NEVER stored in `SystemConfig` or any database table
- `SecureKeyStorage.clearApiKey()` called when user clears all data
- Key is NEVER logged
