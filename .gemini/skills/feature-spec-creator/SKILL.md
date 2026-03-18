---
name: feature-spec-creator
description: Generates standardized technical feature specifications for ScribbleFit (Android & iOS). Triggered when the user asks for a feature spec or uses '/spec'. Ensures alignment with codebase-verified architecture (MVI, Compose, Offline-First).
---

# Feature Spec Creator

This skill generates comprehensive technical specification documents for ScribbleFit features, adhering to the project's architectural principles and development guidelines.

## Workflow

1.  **Analyze the Feature:** Understand the feature's purpose and its impact on the Android and iOS apps.
2.  **Check Designs:** Search for and review relevant UI designs using the **Stitch MCP** tools (`list_projects`, `list_screens`, `get_screen`). Incorporate visual requirements and component hierarchies from these designs into the specification.
3.  **Load Project Context:** Always reference `references/android-project-guidelines.md` and `references/ios-project-guidelines.md` to ensure the "Development Guidelines" section aligns with ScribbleFit's codebase-verified architecture (MVI, Compose, SwiftData, etc.).
4.  **Generate Spec:** Use `assets/spec-template.md` to structure the document.
5.  **Adhere to Principles:**
    *   **Overview:** Provide a clear, technical description.
    *   **User Stories:** Use the "As a... I want to... so that..." format.
    *   **Acceptance Criteria:** Include specific, measurable goals.
    *   **Contextual UI Splitting:** Mandate that the UI must be designed and specified as separate `Header`, `Body`, and `Footer` components.
    *   **AI-First Features:** If a feature involves AI (summaries, parsing, insights), specify integration with `:feature:ai`'s `LLMEngine` (Android) or equivalent LLM integration (iOS). Mandate specific loading and empty states for AI-generated content.
    *   **Design Alignment:** Ensure the "Acceptance Criteria" and "UI" sections reflect the specific components and layouts found in the Stitch designs.
    *   **Development Guidelines:** Specify the tech stack and architectural details (e.g., State/Intent/ViewModel for Android).
    *   **Validation:** Detail a testing strategy that covers unit, integration, and UI tests.

## Triggers
- When the user uses `/spec [feature description]`.
- When the user asks for a "technical spec", "feature specification", or "design document" for a new feature.

## Output
- A Markdown file (usually stored in `specs/[feature-name].md`) with the sections: Overview, User Stories, Acceptance Criteria, Development Guidelines, and Validation.
