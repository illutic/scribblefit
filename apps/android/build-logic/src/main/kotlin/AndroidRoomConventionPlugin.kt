import com.android.build.api.dsl.LibraryExtension
import com.scribblefit.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("androidx.room")
            pluginManager.apply("com.google.devtools.ksp")

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
            }

            dependencies {
                add("api", libs.findLibrary("room-runtime").get())
                add("api", libs.findLibrary("room-ktx").get())
                add("ksp", libs.findLibrary("room-compiler").get())
            }
        }
    }
}
