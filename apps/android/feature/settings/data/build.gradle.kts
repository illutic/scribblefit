plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.feature.settings.data"
}

dependencies {
    implementation(project(":feature:settings:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:config:domain"))
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.android)
}
