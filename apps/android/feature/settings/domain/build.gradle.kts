plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.settings.domain"
}

dependencies {
    implementation(project(":core:config:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(libs.coroutines.core)
}
