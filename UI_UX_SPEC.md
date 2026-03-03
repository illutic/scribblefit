# MASTER SYSTEM PROMPT: GENERATE UI FOR "SCRIBBLEFIT" MOBILE APP

## 1. GLOBAL DESIGN SYSTEM

* **App Concept:** A "Zero-UI" fitness tracker. Users input messy gym shorthand via text or voice,
  and an AI instantly parses it into structured workout data.
* **Aesthetic:** Hyper-minimalist, airy, text-focused. Inspired by the clean, utilitarian UI of the
  ChatGPT mobile app.
* **Color Palette (Light Mode):**
    * Background: Pure White (`#FFFFFF`)
    * Secondary Background (Input pills, cards, chat bubbles): Very Soft Gray (`#F7F7F8`)
    * Primary Text: Rich Black (`#101010`)
    * Secondary Text (Timestamps, axes, placeholders): Mid-Gray (`#8E8EA0`)
    * Accents/Dividers: Barely visible, extremely light gray (`#E5E5EA`). No heavy drop shadows.
* **Typography:** Sans-serif (Inter, SF Pro, or system default). Highly legible, utilizing scale and
  font-weight for hierarchy rather than color.
* **Interaction Design:** Smooth, rounded corners for cards and buttons (border-radius: 12px to
  16px).
* **Processing States:**
    * **The Ghost Bubble (Inline):** 50% opacity for pending/processing entries in the feed. Subtle shimmering "skeleton" effect.
    * **Failure State:** Soft red underline (#FF3B30 at 20% opacity) with a minimalist mid-gray retry icon.
    * **Success State:** Cross-fade transition into structured summary text.

---

## 2. SCREEN SPECIFICATIONS

### Screen 1: The Intelligent Canvas (Home Screen)

* **Layout:** A clean, conversational interface that anticipates the user's needs.
* **Header:** Minimalist top navigation. Dynamic greeting in rich black: "Evening, George."
* **Main Body:** Pure white canvas, populated with contextual, glanceable elements.
* **The "Insight" Feed (Middle):**
    * Soft gray (`#F7F7F8`), borderless text bubbles floating organically.
    * *Processing:* "Ghost Bubbles" showing raw text while the AI parses (e.g., *"Bench 135x5..."*).
    * *Bubble Example:* "You hit chest on Thursday. Ready for a Pull day? 💪"
* **The Scribble Label (Above Input):**
    * Transient, Mid-Gray (`#8E8EA0`) status text. 
    * *Example:* "Scribbling 2 entries..." or "Retrying failed sync..."
* **Quick-Start Prompts (Horizontal Scroll):**
    * Just above the input field, a row of minimalist, outline-only pill buttons for one-tap
      logging.
    * *Pills:* "Repeat last Pull Day", "Log 5k Southsea run", "Rest Day".
* **Input Component (Bottom fixed):**
    * A sleek pill-shaped input field spanning most of the width. Background is `#F7F7F8`.
    * Placeholder text inside: "Message ScribbleFit..."
    * Right-aligned inside the input pill: A minimalist black microphone icon inside a subtle
      circular button for voice dictation.

### Screen 2: The Structured Ledger (Confirmation Screen)

* **Layout:** A feed of parsed data presented as clean, borderless cards.
* **Header:** "Workout Summary" with a small timestamp (e.g., "Today, 18:30"). Location tag: "📍
  PureGym Portsmouth".
* **List Items (The Exercises):**
    * Each exercise is a block separated by whitespace.
    * Title: Bold, rich black (e.g., "Barbell Bench Press").
    * Data rows beneath title: Minimalist text format. e.g., "135 lbs × 5", "135 lbs × 5", "145
      lbs × 3".
    * Notes section below data: Mid-gray italic text. e.g., *"Notes: Swapped incline machine for
      DBs."*
* **Bottom Action:** A wide, primary button (Black background, white text) reading "Save to Ledger".

### Screen 3: The Insights Dashboard (Analytics Home)

* **Layout:** A scrolling feed of macro-level trends flowing naturally with lots of whitespace.
* **Header Area (Top - Sticky):**
    * Left-aligned bold title: "Insights".
    * Segmented control toggle below title: "W", "M" (selected, black text/white pill), "Y".
* **Section 1: AI Summary:**
    * A soft gray borderless text bubble spanning the width.
    * *Text:* "Strong month. You've trained 14 days, and your total volume is up 12%. Leg day
      consistency is your biggest improvement."
* **Section 2: Consistency Tracker:**
    * *Header:* "Workouts per Week".
    * *Chart:* Minimalist vertical bar chart. Solid black bars (`#101010`), rounded tops. No y-axis
      lines.
* **Section 3: Macro Volume Trend:**
    * *Header:* "Total Volume" (Subtitle: "124,500 lbs lifted").
    * *Chart:* Smooth, medium-thickness black line with a faint cool gray gradient fading down. No
      gridlines.
* **Section 4: Muscle Group Focus:**
    * *Header:* "Distribution".
    * *Format:* Clean text rows with thin black progress bars (e.g., "Legs [====  ] 40%").

### Screen 4: Exercise Deep-Dive (Progress Visualizer)

* **Layout:** Clinical data visualization for a single exercise.
* **Header:** Large bold title: "Barbell Bench Press".
* **Sub-navigation:** Pill-shaped toggle menu: "1RM", "Max Weight" (selected), "Volume".
* **Graph Component (Top Half):**
    * Pure white background. Crisp black line charting progress. Faint gray gradient below line. No
      gridlines.
* **Recent History Component (Bottom Half):**
    * Clean list of recent dates and top sets separated by faint dividers. e.g., "Mar 2: 145 lbs ×
      3" | "Feb 24: 135 lbs × 5".

### Screen 5: The Exercise Library

* **Layout:** A scannable, searchable index list.
* **Header:** Left-aligned bold title: "Exercises".
* **Search Component:** Sleek search pill below the title (`#F7F7F8` background).
* **Filter Bar:** Horizontal scroll of text-only pills: "All" (Bold black), "Chest" (Gray), "
  Back", "Legs".
* **List Component:**
    * Vertically scrolling list separated by faint hairline dividers (`#E5E5EA`).
    * *Row Structure:* Bold main title ("Barbell Bench Press"), smaller gray subtitle ("Chest • Last
      logged 2 days ago"). Right-aligned minimalist chevron (`>`).

### Screen 6: Profile & Settings

* **Layout:** A scrollable view focusing on user settings and lifetime stats.
* **Header Area:**
    * Circular avatar placeholder (`#F7F7F8` background with a crisp black "G").
    * Bold text: "George". Subtitle: "Member since March 2026".
* **Stats Grid:**
    * Borderless cards with `#F7F7F8` background. e.g., "Total Workouts: 42", "Lifetime Volume:
      124,500 lbs".
* **Settings List:**
    * Standard text rows with subtle bottom borders.
    * Sections: 
        * "AI Engine":
            * "Parsing Mode": Toggle between "ScribbleFit Cloud" and "Personal API Key".
            * "API Key": Masked input field (only visible if Personal Key is selected).
            * "Provider": Segmented control (OpenAI / Anthropic).
        * "Preferences" (Weight Units, Appearance), 
        * "Data & Storage" (Export Ledger), 
        * "Danger Zone" (Clear All Data in red text `#FF3B30`).