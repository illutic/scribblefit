# Analytics — Google Stitch Spec

## Headline
An AI-generated insights dashboard that surfaces weekly summaries, muscle group breakdowns, and per-exercise trends — all as plain, scannable text with no charts or graphs.

## Primary Stitch Prompt

Design a hyper-minimalist iOS fitness app analytics screen called Insights. The background is pure white (#FFFFFF). No shadows, no gradients, no charts, no graphs, no colored containers, and no accent colors of any kind. All data is communicated through plain text and typographic hierarchy.

At the very top, the screen title "Insights" is displayed in 28pt semibold Rich Black (#101010), left-aligned, with 16pt left margin and 24pt top margin below the status bar.

Directly below the title, with 16pt top spacing, is a full-width AI Suggestion Banner. This is a flat card with 12pt corner radius and a Very Soft Gray (#F7F7F8) background — no border, no shadow. Inside the card: on the left edge, a large emoji such as 🔥 is vertically centered. To the right of the emoji, two lines of 15pt regular Rich Black text display the AI suggestion, for example: "Your weekly volume hit a 4-week high. Recovery looks good — consider adding a 4th set to your bench work." Below the suggestion text, in 12pt regular Mid Gray (#8E8EA0), a timestamp reads "Updated just now". Horizontal padding inside the card is 16pt; vertical padding is 16pt.

Below the banner, with 24pt spacing, is a section header: "This Week" in 17pt semibold Rich Black (#101010), left-aligned.

Below that section header is a flat white Summary Card with 12pt corner radius and a 1pt Light Gray (#E5E5EA) border. Inside the card, a short bulleted list of 3 to 4 highlights in 15pt regular Rich Black, for example:
- 4 sessions completed
- 52,100 lb total volume
- 6 unique exercises

Below those bullet points, a thin 1pt Light Gray horizontal divider runs full-width inside the card. Below the divider, a sub-section label "Muscle Groups" appears in 12pt semibold Mid Gray (#8E8EA0). Below that label, 4 to 5 lines of text show muscle group percentages, for example:
  Chest        38%
  Back          27%
  Shoulders   18%
  Arms          17%
Each line has the muscle group name in 15pt regular Rich Black on the left and the percentage in 15pt regular Mid Gray on the right. Line spacing is 10pt between each muscle group row. Horizontal and vertical padding inside the card is 16pt throughout.

Below the Summary Card, with 24pt spacing, is another section header: "Exercises" in 17pt semibold Rich Black (#101010), left-aligned.

Below this header is a flat list of Exercise Insight Rows with no separators and 16pt vertical spacing between them. Each row is a horizontal strip on the pure white background with three pieces of information: the exercise name — for example "Barbell Bench Press" — in 17pt regular Rich Black on the left; a trend label — for example "↑ Improving" or "→ Stable" or "↓ Declining" — in 15pt regular Mid Gray in the center-right; and an estimated 1RM value — for example "e1RM 198 lb" — in 12pt regular Mid Gray on the far right.

Sample exercise rows: "Barbell Bench Press" / "↑ Improving" / "e1RM 198 lb"; "Back Squat" / "↑ Improving" / "e1RM 315 lb"; "Overhead Press" / "→ Stable" / "e1RM 141 lb"; "Romanian Deadlift" / "→ Stable" / "e1RM 245 lb"; "Pull-up" / "↓ Declining" / "e1RM —".

The entire screen is scrollable. The feel is editorial, data-rich through prose and percentages, completely without decoration.

## Component Inventory

| Element | Shape | Background | Text | Size hint |
|---|---|---|---|---|
| Screen title "Insights" | Flat text | None | 28pt semibold, #101010 | Left-aligned, top |
| AI Suggestion Banner | Rounded rect, 12pt radius | #F7F7F8 | 15pt regular body, 12pt Mid Gray caption | Full width minus 32pt |
| Banner emoji | Text character | None | Large emoji, ~24pt | Left side of banner |
| Banner timestamp | Flat text | None | 12pt regular, #8E8EA0 | Below suggestion body |
| Section header "This Week" | Flat text | None | 17pt semibold, #101010 | Left-aligned |
| Summary card | Rounded rect, 12pt radius | #FFFFFF, 1pt #E5E5EA border | 15pt regular bullets, #101010 | Full width minus 32pt |
| Muscle group row | Flat text pair | None | 15pt regular, name #101010, pct #8E8EA0 | Full width inside card |
| Card internal divider | Horizontal line | #E5E5EA | None | 1pt height |
| "Muscle Groups" sub-label | Flat text | None | 12pt semibold, #8E8EA0 | Left-aligned inside card |
| Section header "Exercises" | Flat text | None | 17pt semibold, #101010 | Left-aligned |
| Exercise insight row | Flat strip | None | 17pt name #101010, 15pt trend #8E8EA0, 12pt e1RM #8E8EA0 | Full width, 16pt spacing |

## State Variations

### Empty State Prompt

Design the Insights analytics screen in its empty state. Pure white background. The screen title "Insights" appears in 28pt semibold Rich Black at the top-left. Below it, the full-width AI Suggestion Banner card in Very Soft Gray (#F7F7F8) with 12pt corner radius shows a 🌱 emoji on the left and two lines of 15pt Rich Black text: "Log at least 3 workouts to unlock your first insight." with a 12pt Mid Gray caption "No data yet." The "This Week" and "Exercises" section headers appear below but all rows beneath them are replaced by a single line of centered 15pt italic Mid Gray text: "Nothing to show yet." The screen has generous whitespace and no decorative elements.

### Loading State Prompt

Design the Insights analytics screen while AI-generated data is loading. Pure white background. "Insights" title at top-left in 28pt semibold Rich Black. The AI Suggestion Banner card in Very Soft Gray shows a ⏳ emoji and the 15pt body text reads "Analyzing your recent sessions…" with a 12pt Mid Gray timestamp "Updating now." Below, under the "This Week" section header, the Summary Card shows two short rounded gray bars in Very Soft Gray representing loading bullet text and two muscle group placeholder rows with similar gray fill bars. Under the "Exercises" section header, four exercise insight row placeholders show short, medium, and long rounded gray bars in Very Soft Gray arranged as name, trend, and e1RM. No text is visible in placeholder rows. Pure white throughout.

### Populated State Prompt

Design the Insights analytics screen with a full week of data. Pure white background. "Insights" at top in 28pt semibold Rich Black. The AI Suggestion Banner in Very Soft Gray shows 🔥 emoji and text: "Your weekly volume hit a 4-week high. Consider adding a 4th set to bench press this cycle." with "Updated just now" in 12pt Mid Gray. Below, the "This Week" section header. The white Summary Card with a 1pt Light Gray border lists bullets: "4 sessions completed", "52,100 lb total volume", "6 unique exercises". Below a divider inside the card, the "Muscle Groups" sub-label in 12pt Mid Gray, then rows: Chest 38%, Back 27%, Shoulders 18%, Arms 17%. Below the card, the "Exercises" section header. Five exercise rows: "Barbell Bench Press" / "↑ Improving" / "e1RM 198 lb"; "Back Squat" / "↑ Improving" / "e1RM 315 lb"; "Overhead Press" / "→ Stable" / "e1RM 141 lb"; "Romanian Deadlift" / "→ Stable" / "e1RM 245 lb"; "Pull-up" / "↓ Declining" / "e1RM —". All on pure white, no dividers between rows.
