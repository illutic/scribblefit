plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.settings.domain"
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    implementation(project(":core:config:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(libs.coroutines.core)
}
