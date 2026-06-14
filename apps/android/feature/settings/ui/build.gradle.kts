plugins {
    id("scribblefit.android.library.compose")
    id("scribblefit.android.hilt")
    id("scribblefit.android.unit.test")
}

android {
    namespace = "com.scribblefit.feature.settings.ui"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":feature:settings:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":core:config:domain"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.viewmodel.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
