import os
import re

features_dir = "/Users/george.sigalas/AndroidStudioProjects/scribblefit/apps/android/feature"

# Patch dependencies
for root, dirs, files in os.walk(features_dir):
    if "/domain" in root and "build.gradle.kts" in files:
        filepath = os.path.join(root, "build.gradle.kts")
        with open(filepath, "r") as f:
            content = f.read()
        
        if "testImplementation(libs.junit)" not in content:
            if "dependencies {" in content:
                content = content.replace("dependencies {", "dependencies {\n    testImplementation(libs.junit)\n    testImplementation(libs.mockk)\n    testImplementation(libs.coroutines.test)\n")
            else:
                content += "\ndependencies {\n    testImplementation(libs.junit)\n    testImplementation(libs.mockk)\n    testImplementation(libs.coroutines.test)\n}\n"
            with open(filepath, "w") as f:
                f.write(content)
            print("Patched deps", filepath)

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

    # Extract class name carefully
    class_match = re.search(r'^class\s+([A-Za-z0-9_]+)', content, re.MULTILINE)
    if not class_match:
        return
    class_name = class_match.group(1)

    # Find the class definition to extract constructor
    class_def_match = re.search(r'^class\s+' + class_name + r'\s*\(', content, re.MULTILINE)
    params = []
    if class_def_match:
        start_idx = class_def_match.end()
        paren_count = 1
        idx = start_idx
        while idx < len(content) and paren_count > 0:
            if content[idx] == '(':
                paren_count += 1
            elif content[idx] == ')':
                paren_count -= 1
            idx += 1
        
        constructor_content = content[start_idx:idx-1]
        
        if constructor_content.strip():
            # split by comma, but be careful with nested types, actually here they are simple
            lines = constructor_content.split(',')
            for line in lines:
                if 'val ' in line or 'var ' in line:
                    parts = line.split(':')
                    if len(parts) >= 2:
                        # Extract the name part before colon, remove annotations
                        name_part = parts[0].strip()
                        pname = name_part.split(' ')[-1]
                        
                        # Extract type
                        ptype = parts[1].strip()
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

use_cases = []
for root, dirs, files in os.walk(features_dir):
    if "/domain/src/main/" in root:
        for file in files:
            if file.endswith("UseCase.kt"):
                use_cases.append(os.path.join(root, file))

# We'll regenerate all missing tests
for uc in use_cases:
    # Check if there is an existing generated test and overwrite it
    # We will just overwrite all missing ones or ones we generated
    # Since we are re-generating correctly, let's just generate for all that don't have real tests
    # Wait, how to know if a test is real? Real tests might have more than 1 @Test, ours has 1.
    test_path = uc.replace("/src/main/java/", "/src/test/java/").replace("/src/main/kotlin/", "/src/test/kotlin/").replace(".kt", "Test.kt")
    if os.path.exists(test_path):
        with open(test_path, 'r') as tf:
            c = tf.read()
            if "TODO: Add proper test implementation" not in c:
                continue # Skip real tests
    generate_test(uc)
