# Ledger — Google Stitch Spec

## Headline
A chronological workout history list where users browse past sessions by month and tap into detailed set-by-set breakdowns.

## Primary Stitch Prompt

Design a hyper-minimalist iOS fitness app history screen called the Ledger. The background is pure white (#FFFFFF). No shadows, no gradients, no colored containers, no accent colors.

At the very top, the screen title "Ledger" is displayed in 28pt semibold Rich Black (#101010), left-aligned, with 16pt left margin and 24pt top margin below the status bar.

Directly below the title, with 8pt spacing, is a month filter label. It reads "March 2026" in 15pt regular Mid Gray (#8E8EA0), left-aligned, with a small downward chevron icon in Mid Gray immediately to its right — indicating it is tappable to change the month. No background, no pill, no border around this label.

Below the month filter, with 24pt vertical spacing, is a flat scrollable list of workout rows. There are no dividers or separators between rows. Each row uses 16pt vertical padding above and below, creating visual breathing room in place of separators.

Each workout row is a single horizontal strip on the pure white background. On the far left, the date string — for example "Mon, Mar 3" — is shown in 15pt regular Mid Gray (#8E8EA0). In the center-left, the workout name — for example "Upper Body A" — is displayed in 17pt regular Rich Black (#101010). On the far right, the total volume — for example "12,450 lb" — is shown in 12pt regular Mid Gray (#8E8EA0). All three elements sit on the same baseline row with no decorative elements.

A sample list includes: "Mon, Mar 3" / "Upper Body A" / "12,450 lb"; then "Wed, Mar 5" / "Lower Body B" / "18,200 lb"; then "Fri, Mar 7" / "Push Day" / "9,800 lb"; then "Sat, Mar 8" / "Pull Day" / "11,100 lb".

When a row is tapped, the screen transitions to a Workout Detail view. The detail screen has a pure white background. The top shows the date as the screen title — for example "Friday, March 7" — in 28pt semibold Rich Black, left-aligned. Below the title, exercises are listed as section headers in 17pt semibold Rich Black — for example "Barbell Bench Press", "Overhead Press", "Tricep Pushdown". Under each section header, individual set rows are shown: the weight and reps — for example "135 lb × 5" — in 17pt regular Rich Black on the left, and the RPE value — for example "RPE 8" — in 15pt regular Mid Gray on the right. Sets within an exercise use 12pt vertical spacing. Between exercises, use 24pt vertical spacing. No list separators, no borders, no colored containers anywhere.

The overall feel is a clean, editorial list — like a well-formatted plain-text log. Airy whitespace carries all the hierarchy.

## Component Inventory

| Element | Shape | Background | Text | Size hint |
|---|---|---|---|---|
| Screen title "Ledger" | Flat text | None | 28pt semibold, #101010 | Left-aligned, 16pt margin |
| Month filter label | Flat text + chevron icon | None | 15pt regular, #8E8EA0 | Left-aligned, below title |
| Month chevron | Small icon | None | #8E8EA0 | ~12pt |
| Workout row — date | Flat text | None | 15pt regular, #8E8EA0 | Far left of row |
| Workout row — name | Flat text | None | 17pt regular, #101010 | Center-left of row |
| Workout row — volume | Flat text | None | 12pt regular, #8E8EA0 | Far right of row |
| Row spacing | Whitespace | None | None | 16pt top and bottom per row |
| Detail screen title | Flat text | None | 28pt semibold, #101010 | Left-aligned |
| Exercise section header | Flat text | None | 17pt semibold, #101010 | Left-aligned |
| Set row — weight × reps | Flat text | None | 17pt regular, #101010 | Left side |
| Set row — RPE | Flat text | None | 15pt regular, #8E8EA0 | Right side |

## State Variations

### Empty State Prompt

Design the Ledger history screen in its empty state. Pure white background. The screen title "Ledger" appears in 28pt semibold Rich Black at the top-left. Below it, the month label "March 2026" with a small chevron in Mid Gray. The list area below is completely blank with generous white space. Centered vertically in the empty list area, show a single line of 15pt italic Mid Gray text: "No workouts logged yet. Head to the Canvas to start." No illustrations, no icons, no decorative elements. Completely minimal.

### Loading State Prompt

Design the Ledger history screen while workout data is loading. Pure white background. The "Ledger" title in 28pt semibold Rich Black appears at top-left. The "March 2026" month label with chevron appears below it. In the list area, show four placeholder rows. Each placeholder row has two rounded gray bars — a short one on the left in Very Soft Gray (#F7F7F8) simulating the date string, and a longer one in the center in Very Soft Gray simulating the workout name. A small short bar on the right simulates the volume text. The bars have no text and use 16pt vertical spacing between rows. All backgrounds remain pure white. No spinners or overlays.

### Populated State Prompt

Design the Ledger history screen with a full month of workout history. Pure white background. "Ledger" title at top-left in 28pt semibold Rich Black. "March 2026" with a down chevron in 15pt Mid Gray below it. Below, a clean list of eight workout rows with 16pt vertical spacing and no dividers: "Mon, Mar 3" / "Upper Body A" / "12,450 lb"; "Wed, Mar 5" / "Lower Body B" / "18,200 lb"; "Fri, Mar 7" / "Push Day" / "9,800 lb"; "Sat, Mar 8" / "Pull Day" / "11,100 lb"; "Mon, Mar 10" / "Upper Body B" / "13,600 lb"; "Wed, Mar 12" / "Lower Body A" / "19,050 lb"; "Fri, Mar 14" / "Push Day" / "10,300 lb"; "Sat, Mar 15" / "Pull Day" / "12,750 lb". Date text in 15pt Mid Gray, workout name in 17pt Rich Black, volume in 12pt Mid Gray on the right. Pure white between all rows.
