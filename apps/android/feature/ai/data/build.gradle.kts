plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.feature.ai.data"
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:coroutines"))
    implementation(project(":core:config:domain"))
    implementation(project(":feature:workout:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.security.crypto)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.mlkit.genai.prompt)
    implementation(libs.coroutines.play.services)
    implementation(libs.slf4j.android)
}
