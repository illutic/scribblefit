plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.workouts.domain"
}

dependencies {
    implementation(project(":core:model"))
}
