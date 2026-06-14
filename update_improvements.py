import re

with open('specs/improvements.md', 'r') as f:
    content = f.read()

resolved = [
    "1. Multi-step database operations lack transactions",
    "2. `clearScribbleExercises` deletes wrong table",
    "3. Exercise deletion cascades are missing",
    "4. Loading state race conditions in CanvasViewModel",
    "5. Edit flow loses unsaved bottom-sheet changes",
    "6. `editingScribbleId` state goes stale",
    "7. Prompt injection in AI engines",
    "11. Business logic in ViewModels",
    "14. InsightsScreen not implemented",
    "15. Insights date range is hardcoded to 1 month",
    "18. No confirmation dialog before scribble deletion",
    "22. AI overview cache key is order-dependent"
]

for title in resolved:
    pattern = re.compile(r'(###\s+)(' + re.escape(title) + r')')
    content = pattern.sub(r'\1[RESOLVED] \2', content)

with open('specs/improvements.md', 'w') as f:
    f.write(content)

print("Updated improvements.md")
