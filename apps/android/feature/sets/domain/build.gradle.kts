plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.sets.domain"
}

dependencies {
    implementation(project(":core:model"))
}
