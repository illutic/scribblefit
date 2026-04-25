plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
    id("scribblefit.android.unit.test")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.feature.ai.data"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:model"))
    implementation(project(":core:coroutines"))
    implementation(project(":core:config:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coroutines.android)
    implementation(libs.mlkit.genai.prompt)
    implementation(libs.slf4j.android)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai)
    implementation(libs.firebase.appcheck.playintegrity)
}
