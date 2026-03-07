plugins {
    id("scribblefit.android.feature")
}

android {
    namespace = "com.scribblefit.feature.canvas"
}

dependencies {
    implementation(project(":feature:canvas:domain"))
    implementation(project(":feature:canvas:data"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:ai:data"))
    implementation(project(":core:designsystem"))
}
