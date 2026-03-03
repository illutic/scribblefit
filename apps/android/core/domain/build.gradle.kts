plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.core.domain"
}

dependencies {
    implementation(project(":core:model"))
}
