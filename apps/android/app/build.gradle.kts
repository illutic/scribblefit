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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    implementation(project(":feature:exercises:data"))
    implementation(project(":feature:exercises:domain"))
    implementation(project(":feature:workouts:data"))
    implementation(project(":feature:workouts:domain"))
    implementation(project(":feature:sets:data"))
    implementation(project(":feature:sets:domain"))
    implementation(project(":feature:scribble:data"))
    implementation(project(":feature:scribble:domain"))
    implementation(project(":feature:insights:domain"))
    implementation(project(":feature:insights:data"))
    implementation(project(":feature:insights:ui"))

    implementation(project(":feature:canvas:ui"))
    implementation(project(":feature:canvas:data"))
    implementation(project(":feature:settings:ui"))
    implementation(project(":feature:settings:data"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.material3)
}
