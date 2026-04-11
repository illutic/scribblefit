# Design System Specification: Editorial Minimalism

## 1. Overview & Creative North Star

The Creative North Star for this design system is **"The Digital Atelier."**

This system moves beyond standard minimalist templates to create a high-end, editorial experience
that feels curated and intentional. We achieve this by rejecting the "boxed-in" nature of
traditional UI. Instead of relying on rigid grids and heavy borders, we use aggressive white space,
dramatic typographic scale, and tactile depth.

The goal is to make the interface feel like a premium physical journal—where the "ink" (Rich Black)
sits with authority on "fine paper" (Pure White), and the navigation feels like "frosted glass"
resting atop the content.



---

## 2. Colors & Tonal Architecture

The palette is monochromatic and sophisticated, relying on light-source logic rather than structural
lines.

### Palette Strategy

* **Primary (#000000):** Reserved for high-impact text and primary actions.

* **Surface (#F9F9F9):** Our "Fine Paper" base.

* **Surface-Container-Lowest (#FFFFFF):** Used for elevated cards to create a "lifted" effect
  against the surface.

* **Surface-Container (#EEEEEE):** Used for subtle grouping and input backgrounds.

### The "No-Line" Rule

**Explicit Instruction:** Prohibit the use of 1px solid borders for sectioning or containment.

Boundaries must be defined solely through background color shifts or the **Spacing Scale**. To
separate a header from a body, transition from `surface` to `surface-container-low`. Visual clarity
comes from the "void" (white space), not the "fence" (lines).

### The Glass & Gradient Rule

Floating elements (Bottom Sheets, Navigation Bars) must use **Glassmorphism**. Apply a
semi-transparent `surface` color with a `backdrop-filter: blur(20px)`. For primary CTAs, a subtle
linear gradient from `primary` (#000000) to `primary_container` (#3C3B3B) adds a "sheen" that
signals premium quality and tactile depth.



---

## 3. Typography: The Editorial Voice

We use **Inter** as our functional backbone, relying on extreme contrast in scale to establish
hierarchy.

| Level | Size | Weight | Tracking | Purpose |

| :--- | :--- | :--- | :--- | :--- |

| **Display-LG** | 3.5rem | 700 (Bold) | -0.04em | Hero statements/Milestones |

| **Headline-SM** | 1.5rem | 600 (Semi) | -0.02em | Section headers |

| **Title-MD** | 1.125rem | 500 (Medium) | -0.01em | Card titles |

| **Body-MD** | 0.875rem | 400 (Regular) | 0 | Standard reading |

| **Label-MD** | 0.75rem | 600 (Semi) | +0.05em | Uppercase Metadata |

**Editorial Note:** Use `Display-LG` sparingly to break the layout. Allow headlines to overlap
slightly with container edges or images to create an asymmetric, "custom-build" feel.



---

## 4. Elevation & Depth

Hierarchy is achieved through **Tonal Layering** and ambient light, not structural shadows.

* **The Layering Principle:** Depth is a "stack."

* Base: `surface` (#F9F9F9)

* Section: `surface-container-low` (#F3F3F4)

* Interactive Card: `surface-container-lowest` (#FFFFFF)

* **Ambient Shadows:** If a card must float (e.g., a modal), use an ultra-diffused shadow:
  `box-shadow: 0 20px 40px rgba(16, 16, 26, 0.06)`. The shadow color must be a tinted version of
  `on-surface` to mimic natural light.

* **The Ghost Border:** If a container lacks contrast against its background, use the
  `outline-variant` token (#C6C6C6) at **15% opacity**. This creates a "suggestion" of a boundary
  that disappears upon focus.

---

## 5. Components

### Buttons

* **Primary:** Solid `primary` (#000000) with `on-primary` (#E5E2E1) text. **Shape:** Full pill (
  `9999px`).

* **Secondary:** `surface-container-high` (#E8E8E8) background. No border.

* **Tertiary:** Ghost style. Text only in `primary`, using `label-md` for an authoritative, compact
  look.

### Input Fields

* **Style:** Fully rounded pills (`full`).

* **Color:** `surface-container` (#EEEEEE).

* **State:** On focus, the background shifts to `surface-container-lowest` (#FFFFFF) with a 2pt "
  Ghost Border."

* **Labels:** Use `label-sm` in `mid-gray` (#8E8EA0), positioned 0.7rem (`spacing-2`) above the
  input.

### Cards & Lists

* **Constraint:** Zero divider lines.

* **Cards:** 12pt (`DEFAULT` / 1rem) rounded corners. Use vertical white space (`spacing-6`) to
  separate list items.

* **Nesting:** Place a `surface-container-lowest` (#FFFFFF) card inside a `surface-container` (
  #EEEEEE) section to create a soft "inset" feel.

### Navigation Glass (iOS Style)

* The bottom navigation bar must be a floating pill or a full-width blur.

* **Specs:** `surface` at 80% opacity, `backdrop-filter: blur(24px)`.

---

## 6. Do’s and Don’ts

### Do:

* **Do** use extreme white space. If a section feels crowded, double the padding using `spacing-10`
  or `spacing-12`.

* **Do** use asymmetry. Align a headline to the left but a sub-label to the far right to create a
  dynamic visual path.

* **Do** use "Optical Centering." Items inside pill buttons should be shifted 1-2px higher to
  compensate for the curve.

### Don’t:

* **Don’t** use pure black (#000000) for large blocks of body text; use `on-surface` (#1A1C1C) to
  reduce eye strain.

* **Don’t** use 100% opaque borders. They break the "Atelier" feel and make the app look like a
  bootstrap template.

* **Don’t** use standard icons. Use "Thin Stroke" (1px or 1.5px) minimalist icons to match the
  editorial weight of the typography.