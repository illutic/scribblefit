plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.feature.canvas.data"
}

dependencies {
    implementation(project(":feature:canvas:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:ai:data"))
    implementation(project(":feature:ledger"))
    implementation(project(":core:database"))

    implementation(libs.hilt.android)
    implementation(libs.kotlinx.serialization.json)
}
