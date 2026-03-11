plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.scribble.domain"
}

dependencies {
    implementation(project(":feature:workout:domain"))
    implementation(project(":core:coroutines"))
    implementation(project(":feature:ai:domain"))
    implementation(libs.coroutines.core)
    implementation(libs.slf4j.android)
}
