plugins {
    id("scribblefit.android.application.compose")
    id("scribblefit.android.hilt")
}

android {
    namespace = "com.scribblefit.app"

    defaultConfig {
        applicationId = "com.scribblefit.app"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":feature:canvas:ui"))
    implementation(project(":feature:canvas:domain"))
    implementation(project(":feature:canvas:data"))
    implementation(project(":feature:ledger"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:ai:data"))
    implementation(project(":feature:profile:ui"))
    implementation(project(":feature:profile:domain"))
    implementation(project(":feature:profile:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
}
