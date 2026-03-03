plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.core.model"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
