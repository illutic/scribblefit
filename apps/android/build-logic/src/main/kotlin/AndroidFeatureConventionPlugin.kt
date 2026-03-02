import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("scribblefit.android.library.compose")
            pluginManager.apply("scribblefit.android.hilt")

            dependencies {
                add("implementation", project(":core:database"))
                add("implementation", project(":core:network"))
            }
        }
    }
}
