plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.analytics.domain"
}

dependencies {
    implementation(project(":feature:ai:domain"))
    implementation(libs.coroutines.core)
}
