plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
}

android {
    namespace = "com.scribblefit.core.config.data"
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:coroutines"))
    implementation(project(":core:config:domain"))
    implementation(libs.coroutines.android)
    implementation(libs.slf4j.android)
}
