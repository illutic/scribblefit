plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
}

android {
    namespace = "com.scribblefit.feature.canvas.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:coroutines"))
    implementation(project(":feature:canvas:domain"))
    implementation(project(":feature:scribble:domain"))
    implementation(project(":feature:exercises:domain"))
    implementation(project(":feature:ai:domain"))
}
