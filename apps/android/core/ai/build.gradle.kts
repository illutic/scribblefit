plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.core.ai"
}

dependencies {
    implementation(project(":core:model"))
}
