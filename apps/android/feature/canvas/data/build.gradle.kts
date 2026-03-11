plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.feature.canvas.data"
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:coroutines"))
    implementation(project(":feature:workout:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:scribble:domain"))
    implementation(project(":feature:canvas:domain"))
    implementation(project(":feature:ledger"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coroutines.core)
}
