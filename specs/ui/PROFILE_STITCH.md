# Profile/Settings — Google Stitch Spec

## Headline
A clean settings screen for configuring the AI engine, entering API keys, and managing preferences and data — organized into labeled sections with no decorative chrome.

## Primary Stitch Prompt

Design a hyper-minimalist iOS fitness app settings screen. The background is pure white (#FFFFFF). No shadows, no gradients, no colored section backgrounds, no accent colors. All rows sit directly on the white background. Section grouping is communicated by 12pt semibold Mid Gray (#8E8EA0) section header labels only — no card containers or inset grouped table styling.

At the very top, the screen title "Settings" is displayed in 28pt semibold Rich Black (#101010), left-aligned, with 16pt left margin and 24pt top margin below the status bar.

The first section is the "AI Engine" section. The section begins with a label "AI ENGINE" in 12pt semibold Mid Gray (#8E8EA0), uppercase, left-aligned with 16pt margin, 24pt below the title. A 1pt Light Gray (#E5E5EA) divider runs full-width below the section label.

Inside this section are three rows, each with 16pt horizontal margin, 16pt vertical padding, and a 1pt Light Gray bottom divider:

Row 1 — Provider: the label "Provider" in 17pt regular Rich Black (#101010) on the left. On the right, the current value "Gemini" in 15pt regular Mid Gray (#8E8EA0), followed immediately by a small right-pointing chevron icon in Mid Gray. The chevron indicates this row is tappable.

Row 2 — Model: the label "Model" in 17pt regular Rich Black on the left. On the right, "gemini-2.5-pro" in 15pt regular Mid Gray, then a small right-pointing chevron in Mid Gray.

Row 3 — API Key: the label "API Key" in 17pt regular Rich Black on the left. In the center of the row, a masked text field showing "••••••••••••7F3A" in 15pt regular Mid Gray — the field has no visible border, it appears inline. On the far right, a small pill-shaped button with the label "Set Key" in 15pt semibold Rich Black, using a Very Soft Gray (#F7F7F8) fill and a 1pt Light Gray border, 8pt corner radius, 8pt horizontal padding. No chevron on this row.

The second section is "Preferences". Section label "PREFERENCES" in 12pt semibold Mid Gray, uppercase, left-aligned, 32pt below the last AI Engine row. A 1pt Light Gray divider runs below the section label.

Inside this section are two rows:

Row 1 — Weight Unit: the label "Weight Unit" in 17pt regular Rich Black on the left. On the right, a small inline segmented toggle showing two options "lbs" and "kg" — the active option "lbs" has a Rich Black background with white 13pt semibold text; the inactive option "kg" has no background with Mid Gray 13pt text. The toggle uses a Very Soft Gray pill container with 6pt corner radius, no border.

Row 2 — Theme: the label "Theme" in 17pt regular Rich Black on the left. On the right, the current value "System" in 15pt regular Mid Gray, followed by a small right-pointing chevron in Mid Gray.

Both rows have 16pt horizontal and vertical padding, with 1pt Light Gray bottom dividers.

The third section is "Data". Section label "DATA" in 12pt semibold Mid Gray, uppercase, left-aligned, 32pt below the last Preferences row. A 1pt Light Gray divider runs below the section label.

Inside this section is a single row: no icon, no value, no chevron. The label reads "Clear All Data" in 17pt regular Danger Red (#FF3B30), left-aligned with 16pt margin. 16pt vertical padding above and below. A 1pt Light Gray divider below.

The screen is scrollable but content fits on one screen for most devices. The overall feel is a clean, native-feeling plain list — typographic only, completely undecorated.

## Component Inventory

| Element | Shape | Background | Text | Size hint |
|---|---|---|---|---|
| Screen title "Settings" | Flat text | None | 28pt semibold, #101010 | Left-aligned, top |
| Section header label | Flat text | None | 12pt semibold uppercase, #8E8EA0 | Left-aligned, above section |
| Section divider | Horizontal line | #E5E5EA | None | 1pt height, full width |
| Settings row — label | Flat text | None | 17pt regular, #101010 | Left side of row |
| Settings row — value | Flat text | None | 15pt regular, #8E8EA0 | Right side of row |
| Row chevron | Small icon | None | #8E8EA0 | ~12pt, far right |
| Row bottom divider | Horizontal line | #E5E5EA | None | 1pt height |
| API key masked field | Inline text | None | 15pt regular, #8E8EA0 | Center of API Key row |
| "Set Key" pill button | Rounded rect, 8pt radius | #F7F7F8, 1pt #E5E5EA border | 15pt semibold, #101010 | Compact, right of row |
| Weight unit toggle | Pill container, 6pt radius | #F7F7F8 (container), #101010 (active segment) | 13pt semibold active white, 13pt Mid Gray inactive | Right side of row |
| "Clear All Data" row label | Flat text | None | 17pt regular, #FF3B30 | Left-aligned, no icon |

## State Variations

### Empty State Prompt

Design the Settings screen in its initial unconfigured state. Pure white background. "Settings" title in 28pt semibold Rich Black at top-left. The AI Engine section shows all three rows — Provider, Model, API Key — but the API Key row shows "Not set" in 15pt Mid Gray where the masked field would be, and the "Set Key" button is clearly visible on the right in a Very Soft Gray pill. The Provider row shows "Not selected" in 15pt Mid Gray. The Preferences section shows Weight Unit toggle with "lbs" active and Theme showing "System". The Data section shows the "Clear All Data" label in Danger Red. No modal sheets, no alerts. Pure white, fully minimal.

### Loading State Prompt

Design the Settings screen while saved preferences are being loaded from local storage. Pure white background. "Settings" title at top in 28pt semibold Rich Black. In the AI Engine section, the Provider and Model rows show short rounded gray bars in Very Soft Gray (#F7F7F8) on the right side where the values normally appear — simulating loading text. The API Key row shows a short gray bar instead of the masked field, with the "Set Key" button still visible as a Very Soft Gray pill. The Preferences and Data sections appear fully rendered and static — Weight Unit toggle visible, Theme row visible, "Clear All Data" in Danger Red. Whitespace is generous throughout.

### Populated State Prompt

Design the Settings screen fully configured. Pure white background. "Settings" title in 28pt semibold Rich Black at top-left. AI Engine section: Provider row shows "Gemini" with chevron; Model row shows "gemini-2.5-pro" with chevron; API Key row shows "••••••••••••7F3A" in 15pt Mid Gray in the center and a "Set Key" pill button in Very Soft Gray on the right. Preferences section: Weight Unit toggle shows "lbs" as the active segment with Rich Black fill and white text, "kg" inactive in Mid Gray; Theme row shows "System" with chevron. Data section: a single "Clear All Data" label in 17pt Danger Red, left-aligned. All rows have clean 1pt Light Gray bottom dividers. The screen sits on pure white throughout, fully readable, no decorative elements.
