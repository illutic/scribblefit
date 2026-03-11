plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.core.config.domain"
}

dependencies {
    implementation(libs.coroutines.core)
}
