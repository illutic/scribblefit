plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
}

android {
    namespace = "com.scribblefit.feature.scribble.data"
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:coroutines"))
    implementation(project(":feature:workout:domain"))
    implementation(project(":core:config:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:scribble:domain"))
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.slf4j.android)
}
