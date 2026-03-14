plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.scribble.domain"
}

dependencies {
    implementation(project(":core:model"))
}
