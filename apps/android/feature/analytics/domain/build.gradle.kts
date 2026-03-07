plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.analytics.domain"
}

dependencies {
    implementation(project(":core:ai"))
    implementation(project(":core:model"))
    implementation(libs.coroutines.core)
}
