import re

with open('specs/improvements.md', 'r') as f:
    content = f.read()

resolved = [
    "8. No token-limit enforcement on AI prompts",
    "9. Silent failures in use case invocations",
    "10. Scribble status enum crash on unknown value",
    "13. Missing error and loading states in Settings UI"
]

for title in resolved:
    pattern = re.compile(r'(###\s+)(' + re.escape(title) + r')')
    content = pattern.sub(r'\1[RESOLVED] \2', content)

with open('specs/improvements.md', 'w') as f:
    f.write(content)

print("Updated improvements.md again")
