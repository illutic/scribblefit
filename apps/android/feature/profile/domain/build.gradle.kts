plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.profile.domain"
}

dependencies {
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:ledger"))
    implementation(libs.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
}
