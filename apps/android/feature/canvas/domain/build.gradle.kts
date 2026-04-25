plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.canvas.domain"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:coroutines"))
    implementation(project(":core:common"))
    implementation(project(":feature:scribble:domain"))
    implementation(project(":feature:exercises:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(libs.slf4j.android)
}
