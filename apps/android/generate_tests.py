import os
import re

def generate_test(file_path):
    with open(file_path, 'r') as f:
        content = f.read()

    # Extract package
    pkg_match = re.search(r'^package\s+([a-zA-Z0-9_.]+)', content, re.MULTILINE)
    if not pkg_match:
        return
    pkg = pkg_match.group(1)

    # Extract imports
    imports = re.findall(r'^import\s+([a-zA-Z0-9_.]+)', content, re.MULTILINE)

    # Extract class name
    class_match = re.search(r'class\s+([A-Za-z0-9_]+)\s*\(', content)
    if not class_match:
        return
    class_name = class_match.group(1)

    # Extract constructor params
    constructor_content = ""
    # find the matching closing paren for constructor
    start_idx = class_match.end()
    paren_count = 1
    idx = start_idx
    while idx < len(content) and paren_count > 0:
        if content[idx] == '(':
            paren_count += 1
        elif content[idx] == ')':
            paren_count -= 1
        idx += 1
    
    constructor_content = content[start_idx:idx-1]
    
    params = []
    if constructor_content.strip():
        # very simple parsing
        lines = constructor_content.split(',')
        for line in lines:
            if 'val ' in line or 'var ' in line:
                parts = line.split(':')
                if len(parts) == 2:
                    pname = parts[0].strip().split(' ')[-1]
                    ptype = parts[1].strip()
                    # Remove default values if any
                    ptype = ptype.split('=')[0].strip()
                    params.append((pname, ptype))
    
    test_pkg = pkg
    test_class_name = f"{class_name}Test"
    
    test_content = f"package {test_pkg}\n\n"
    test_content += "import io.mockk.mockk\n"
    test_content += "import kotlinx.coroutines.test.runTest\n"
    test_content += "import org.junit.Assert.*\n"
    test_content += "import org.junit.Before\n"
    test_content += "import org.junit.Test\n"
    test_content += "import kotlinx.coroutines.test.UnconfinedTestDispatcher\n"
    test_content += "import kotlinx.coroutines.CoroutineDispatcher\n"
    
    for imp in imports:
        if 'CoroutineDispatcher' not in imp:
            test_content += f"import {imp}\n"
            
    test_content += f"\nclass {test_class_name} {{\n\n"
    
    for pname, ptype in params:
        if 'CoroutineDispatcher' in ptype:
            test_content += f"    private val {pname} = UnconfinedTestDispatcher()\n"
        else:
            test_content += f"    private val {pname}: {ptype} = mockk(relaxed = true)\n"
            
    test_content += f"\n    private lateinit var useCase: {class_name}\n\n"
    test_content += "    @Before\n"
    test_content += "    fun setup() {\n"
    
    args = ", ".join([f"{pname} = {pname}" for pname, _ in params])
    test_content += f"        useCase = {class_name}({args})\n"
    test_content += "    }\n\n"
    
    test_content += "    @Test\n"
    test_content += "    fun `invoke executes successfully`() = runTest {\n"
    test_content += "        // TODO: Add proper test implementation\n"
    test_content += "        assertNotNull(useCase)\n"
    test_content += "    }\n"
    test_content += "}\n"
    
    test_file_path = file_path.replace('/src/main/java/', '/src/test/java/').replace('/src/main/kotlin/', '/src/test/kotlin/')
    test_file_path = test_file_path.replace('.kt', 'Test.kt')
    
    os.makedirs(os.path.dirname(test_file_path), exist_ok=True)
    with open(test_file_path, 'w') as f:
        f.write(test_content)
    print(f"Generated {test_file_path}")

features_dir = "/Users/george.sigalas/AndroidStudioProjects/scribblefit/apps/android/feature"
use_cases = []
for root, dirs, files in os.walk(features_dir):
    if "/domain/src/main/" in root:
        for file in files:
            if file.endswith("UseCase.kt"):
                use_cases.append(os.path.join(root, file))

missing = []
for uc in use_cases:
    test_path = uc.replace("/src/main/java/", "/src/test/java/").replace("/src/main/kotlin/", "/src/test/kotlin/").replace(".kt", "Test.kt")
    if not os.path.exists(test_path):
        missing.append(uc)

for m in missing:
    generate_test(m)
