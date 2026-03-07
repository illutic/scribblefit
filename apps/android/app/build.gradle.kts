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
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:canvas:ui"))
    implementation(project(":feature:canvas:data"))
    implementation(project(":feature:ledger"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:ai:data"))
    implementation(project(":feature:analytics:data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.kotlinx.serialization.json)
}
