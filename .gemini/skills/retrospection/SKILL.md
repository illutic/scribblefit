---
name: retrospection
description: Analyzes completed feature implementations and specifications to identify architectural improvements, workflow optimizations, and patterns that should be formalized. Use this skill after finishing a feature or when significant learnings occur to update the project guidelines and expert subagents.
---

# Retrospection Skill

This skill helps maintain the ScribbleFit project's technical excellence by formalizing learnings from the development process into the project's permanent guidelines.

## Workflow

1.  **Select Target Feature:** Identify a recently completed feature (e.g., in `specs/` and corresponding implementation in `apps/`).
2.  **Review Specification vs. Implementation:**
    *   Compare the `specs/[feature].md` with the final code.
    *   Identify any "course corrections" or architectural adjustments made during implementation.
    *   Look for recurring issues (e.g., boilerplate, testing gaps, DI complexity).
3.  **Identify Formalizations:**
    *   **New Patterns:** Any clean abstraction that should be reused?
    *   **Clarifications:** Any part of the guidelines that was ambiguous or caused confusion?
    *   **Stricter Mandates:** Any areas where more discipline is needed (e.g., zero hardcoding, testing)?
4.  **Update Project Guidelines:**
    *   Update `.gemini/skills/feature-spec-creator/references/android-project-guidelines.md` or `ios-project-guidelines.md`.
    *   Update `.gemini/skills/spec-implementer/references/android-guidelines.md` or `ios-guidelines.md`.
5.  **Sync Expert Subagents:**
    *   Update `.gemini/agents/android-expert.md` or `.gemini/agents/ios-expert.md` to reflect the refined mandates.
6.  **Verify Updates:** Ensure the guidelines remain concise, actionable, and consistent across skills.

## Guidelines for Updates
*   **Concise is Key:** Don't add fluff. Only add mandates that improve code quality or efficiency.
*   **No Base Classes:** Maintain the "autonomous" architecture principle.
*   **Contextual UI Splitting:** Refine the component boundaries based on what worked.
*   **Zero Hardcoding:** Ensure string resolution patterns are clearly defined.
*   **Layer Separation:** Reinforce the Domain -> Data -> UI flow and its verification.

## Triggers
- When the user asks to "retrospect on the [feature] implementation".
- When the user asks to "update guidelines based on our recent work".
- When you identify a repeatable pattern that isn't yet formalized in the project standards.
