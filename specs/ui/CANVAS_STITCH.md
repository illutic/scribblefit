# Canvas — Google Stitch Spec

## Headline
The main scribble input screen where users type raw gym shorthand and see AI-parsed workout confirmations in a live feed.

## Primary Stitch Prompt

Design a hyper-minimalist iOS fitness app home screen called the Canvas. The background is pure white (#FFFFFF). There are no shadows, no gradients, no colored containers, and no accent colors of any kind.

At the very top, a status bar area is followed by a top navigation row. On the left, the app name "ScribbleFit" is displayed in 28pt semibold Rich Black (#101010) text. On the right, a circular avatar placeholder 36pt in diameter uses a Very Soft Gray (#F7F7F8) fill with the initials "GS" in 15pt Mid Gray (#8E8EA0).

Below the top row, with 16pt top margin, is a scrollable feed of cards grouped by date. Each date group begins with a date label — for example "Today, March 8" — in 12pt Mid Gray (#8E8EA0) regular text, left-aligned, with 24pt top spacing above it.

Within each date group there are three card types:

First, a Confirmation Card: a flat white card with 12pt corner radius and a very light gray border (#E5E5EA, 1pt). Inside, the exercise name — for example "Barbell Bench Press" — is shown in 17pt semibold Rich Black on the left. In the top-right corner of the card is the label "✓ Logged" in 12pt Mid Gray. Below the exercise name is a set summary in 15pt regular Mid Gray, for example "3 sets · 135 lb · 5 reps". Vertical padding inside the card is 14pt; horizontal padding is 16pt.

Second, a Scribble Card: a fully rounded pill shape using Very Soft Gray (#F7F7F8) background. The raw user text "OHP 95x5x3" is shown in 15pt regular Rich Black on the left. At the right end of the pill, a small 8pt diameter circle acts as a status dot — Mid Gray (#8E8EA0) for processing, Danger Red (#FF3B30) for failed. Vertical padding is 12pt; horizontal padding is 18pt.

Third, a Prompt Card: no background, no border, no fill. An emoji like 💡 sits on the far left, followed by 2 lines of italic 15pt Mid Gray suggestion text, for example "Try logging your warm-up sets too — it helps track volume over time." 16pt vertical padding above and below.

At the very bottom of the screen, pinned above the home indicator with 16pt margin on all sides, is a full-width pill-shaped input field in Very Soft Gray (#F7F7F8). The placeholder text "What did you lift today?" is shown in 15pt Mid Gray inside the pill. On the right end of the pill, a send arrow icon in Rich Black (#101010) sits inside a small 32pt circle at the same gray fill. The pill height is 52pt and it spans the full width minus 16pt on each side.

The overall feel is airy, generous whitespace, text-focused, and completely undecorated.

## Component Inventory

| Element | Shape | Background | Text | Size hint |
|---|---|---|---|---|
| Screen title "ScribbleFit" | Flat text | None | 28pt semibold, #101010 | Left-aligned, top bar |
| Avatar placeholder | Circle | #F7F7F8 | 15pt #8E8EA0 initials | 36pt diameter |
| Date group label | Flat text | None | 12pt regular, #8E8EA0 | Left-aligned |
| Confirmation card | Rounded rect, 12pt radius | #FFFFFF, 1pt #E5E5EA border | 17pt semibold exercise name, 15pt Mid Gray summary | Full width minus 32pt |
| "✓ Logged" badge | Flat text | None | 12pt regular, #8E8EA0 | Top-right of card |
| Scribble pill card | Fully rounded capsule | #F7F7F8 | 15pt regular, #101010 | Full width minus 32pt, 52pt tall |
| Status dot | Circle | #8E8EA0 or #FF3B30 | None | 8pt diameter |
| Prompt suggestion row | None | None | 15pt italic, #8E8EA0 | Full width, emoji left |
| Input pill field | Fully rounded capsule | #F7F7F8 | 15pt regular, #8E8EA0 placeholder | Full width minus 32pt, 52pt tall |
| Send button | Circle | #F7F7F8 | Arrow icon, #101010 | 32pt diameter |

## State Variations

### Empty State Prompt

Design the Canvas home screen in its empty state. The background is pure white. At the top, the "ScribbleFit" title appears in 28pt semibold Rich Black on the left and a circular avatar placeholder is on the right. The scrollable feed area in the middle is completely blank with generous whitespace. Centered vertically in the empty feed, show a single prompt card: no background, a barbell emoji 🏋️ on the left, followed by 2 lines of italic 15pt Mid Gray text reading "Start scribbling. Type your first set below." At the bottom, the Very Soft Gray pill input field with placeholder text "What did you lift today?" is pinned with 16pt margins.

### Loading State Prompt

Design the Canvas home screen while AI is actively processing a new entry. The background is pure white. At top, "ScribbleFit" title and avatar appear as normal. In the feed, a recent Scribble Card pill in Very Soft Gray shows the raw text "Squat 225x5x5" on the left and a Mid Gray 8pt status dot on the right — indicating processing in progress. Below it, a faint skeleton placeholder card in Very Soft Gray with 12pt corner radius shows two lines of rounded gray bars suggesting text is loading (no actual text, just gray fill bars). The bottom input pill is visible but the send arrow is replaced by a small activity spinner in Mid Gray. All spacing is airy and white.

### Populated State Prompt

Design the Canvas home screen with a rich feed of workout entries. Pure white background. Top bar has "ScribbleFit" on the left and avatar on the right. The feed begins with a "Today, March 8" date label in 12pt Mid Gray. Below it: one Confirmation Card for "Barbell Bench Press" showing "3 sets · 135 lb · 5 reps" and "✓ Logged" top-right; then one Confirmation Card for "Overhead Press" showing "3 sets · 95 lb · 5 reps". Then a prompt card with 💡 emoji and italic suggestion: "Your bench volume is up 8% this week." Further down, a "Yesterday, March 7" date label followed by two more Confirmation Cards for "Back Squat" and "Romanian Deadlift". At the bottom the input pill reads "What did you lift today?" with the send arrow. Generous 16pt spacing between all elements.
