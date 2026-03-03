plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.core.ai.data"
}

dependencies {
    implementation(project(":core:ai"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
}
