plugins {
    id("scribblefit.android.library.compose")
    id("scribblefit.android.hilt")
}

android {
    namespace = "com.scribblefit.feature.exercises.ui"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:common"))
    implementation(project(":core:config:domain"))
    implementation(project(":feature:exercises:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:workouts:domain"))

    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.viewmodel.compose)
    implementation(libs.hilt.navigation.compose)
    
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
