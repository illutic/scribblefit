plugins {
    id("scribblefit.android.library.compose")
    id("scribblefit.android.hilt")
}

android {
    namespace = "com.scribblefit.feature.profile.ui"
}

dependencies {
    implementation(project(":feature:profile:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:ledger"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
}
