plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.feature.ai.data"
}

dependencies {
    implementation(project(":feature:ai:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.hilt.android)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.ktor.serialization.kotlinx.json)
}

tasks.withType<Test> {
    testLogging {
        showStandardStreams = true
    }
}
