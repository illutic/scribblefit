plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.feature.profile.data"
}

dependencies {
    implementation(project(":feature:profile:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:ai:data"))
    implementation(project(":feature:ledger"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))

    implementation(libs.hilt.android)
    implementation(libs.room.ktx)
    implementation(libs.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
}
