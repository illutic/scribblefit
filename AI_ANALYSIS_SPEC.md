# AI ANALYSIS SPECIFICATION: SCRIBBLEFIT

## 1. OVERVIEW
The AI Analysis engine is the "brain" of ScribbleFit. While the AI Parser handles data entry, the Analysis engine transforms raw historical data into proactive coaching, trend visualization, and exercise-level intelligence. It operates strictly **offline-first**, utilizing local summaries and event-driven LLM processing.

---

## 2. CORE INTELLIGENCE MODULES

### A. The Predictive Canvas (Proactive Suggestions)
*   **Objective:** Anticipate what the user is going to train before they type a single word.
*   **Logic:** 
    1.  Analyze the last 4 weeks of workout frequency and day-of-week patterns.
    2.  Identify "Split" patterns (e.g., PPL, Upper/Lower, Full Body).
    3.  Check "Recovery Lag" (time since a specific muscle group was last hit).
*   **Output:** A short, encouraging natural language string.
    *   *Example:* "Monday is usually a Heavy Push day. Ready to bench? 🔥"
    *   *Example:* "You haven't hit Legs in 6 days. Leg Day suggested. 🍗"

### B. Time-Series Summaries (Insights Screen)
*   **Objective:** Provide a human-readable high-level overview of progress for a specific window (Week, Month, Year).
*   **Metrics Analyzed:**
    *   **Volume Change:** Delta % compared to the previous period.
    *   **Consistency:** Workouts per week vs. goal.
    *   **Focus Area:** Which muscle groups dominated the period (using `Exercise_Dictionary` mappings).
*   **Output:** An AI-generated summary bubble.
    *   *Example:* "Strong month. You've trained 14 days, and your total volume is up 12%. Back and Biceps were your focus area."

### C. Exercise Intelligence (Deep-Dive)
*   **Objective:** Deep analysis of a specific movement to identify plateaus or breakthroughs.
*   **Metrics Analyzed:**
    *   **Estimated 1RM Trends:** Using the Brzycki or Epley formula on top sets.
    *   **Volume-Load Curve:** Tracking intensity over time.
    *   **PR Detection:** Automatic identification of "All-Time High" weight or reps.
*   **Output:** Insight labels and trend data for the Progress Visualizer.

---

## 3. DATA ARCHITECTURE & FLOW

### Input Data
The AI Analysis engine reads from:
*   `Workout_Logs`: Dates, total volume, and locations.
*   `Sets`: Specific weight/reps for each exercise.
*   `Exercise_Dictionary`: Metadata (muscle groups, movement patterns).

### Output Storage (`Insights_Cache`)
To ensure zero-latency in the UI, AI analysis results are cached locally.
*   **Schema:**
    *   `key`: Unique identifier (e.g., `suggested_workout`, `monthly_summary_2024_05`).
    *   `json_data`: The AI-generated analysis or suggestion.
    *   `created_at`: Timestamp to handle cache expiration.

---

## 4. LLM PROMPT STRATEGIES

### Suggester Prompt (Home Screen)
```text
Context: Last workout was Back/Biceps on Thursday. Today is Monday.
History: Usually hits Chest on Mondays.
Task: Suggest today's workout in 1 short sentence with an emoji.
```

### Summarizer Prompt (Insights Screen)
```text
Data: [JSON list of last 30 days of workouts]
Task: Generate a 2-sentence summary highlighting the biggest win and the most worked muscle group.
```

---

## 5. EVENT-DRIVEN TRIGGERS

Analysis is not updated on every screen view. To preserve battery and minimize LLM calls, it is triggered by:
1.  **Ledger Commit:** Every time a `ParsedWorkout` is saved to the `Workout_Logs`, a background job triggers a refresh of the `Insights_Cache`.
2.  **App Launch (Daily):** The Home Screen suggestion is recalculated once per 24 hours or on the first launch of the day.
3.  **Manual Refresh:** User pulls to refresh on the Insights screen.

---

## 6. OFFLINE-FIRST ARCHITECTURE (REVISITED)

1.  **Local-First Parsing:** Priority 1 is using Gemini Nano (Android) or Apple Intelligence (iOS).
2.  **Heuristic Fallback:** If LLM is unavailable, use hardcoded logic (e.g., "Repeat last workout") to ensure the UI is never empty.
3.  **Background Sync:** Analysis jobs are enqueued via `WorkManager` (Android) or `BGTaskScheduler` (iOS) with low priority to avoid impacting UI performance.
