plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.workout.domain"
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.slf4j.android)
}
