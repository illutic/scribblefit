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
    implementation(project(":core:navigation"))
    implementation(project(":core:config:data"))
    implementation(project(":core:config:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:ai:data"))
    implementation(project(":feature:canvas:domain"))
    implementation(project(":feature:canvas:data"))
    implementation(project(":feature:canvas:ui"))
    implementation(project(":feature:ledger"))
    implementation(project(":feature:analytics:domain"))
    implementation(project(":feature:analytics:data"))
    implementation(project(":feature:profile:domain"))
    implementation(project(":feature:profile:data"))
    implementation(project(":feature:profile:ui"))
    implementation(project(":feature:scribble:data"))
    implementation(project(":feature:scribble:domain"))
    implementation(project(":feature:workout:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
}
