plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.library.compose")
    id("scribblefit.android.hilt")
    id("scribblefit.android.compose.test")
    id("scribblefit.android.unit.test")
}

android {
    namespace = "com.scribblefit.feature.canvas.ui"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:navigation"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:canvas:domain"))
    implementation(project(":feature:scribble:domain"))
    implementation(project(":feature:insights:domain"))
    implementation(project(":feature:exercises:domain"))
    implementation(project(":feature:sets:domain"))
    implementation(project(":core:config:domain"))

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.viewmodel.compose)
    implementation(libs.androidx.material.icons)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.hilt.navigation.compose)
}
