plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.ai.domain"
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
}
