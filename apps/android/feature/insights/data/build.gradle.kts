plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
    id("scribblefit.android.unit.test")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.feature.insights.data"
}

dependencies {
    implementation(project(":feature:insights:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:scribble:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:coroutines"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.kotlinx.serialization.json)
}
