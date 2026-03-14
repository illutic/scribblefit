plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.exercises.domain"
}

dependencies {
    implementation(project(":core:model"))
}
