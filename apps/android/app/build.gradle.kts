plugins {
    id("scribblefit.android.application.compose")
    id("scribblefit.android.hilt")
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.performance)
}

android {
    namespace = "com.scribblefit.app"

    defaultConfig {
        applicationId = "com.scribblefit.app"
        val ciBuildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 0
        versionCode = ciBuildNumber
        versionName = "1.0.$ciBuildNumber"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_FILE") ?: "../../keystore/release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:config:data"))
    implementation(project(":core:config:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:ai:data"))
    implementation(project(":feature:exercises:data"))
    implementation(project(":feature:exercises:domain"))
    implementation(project(":feature:exercises:ui"))
    implementation(project(":feature:sets:data"))
    implementation(project(":feature:sets:domain"))
    implementation(project(":feature:scribble:data"))
    implementation(project(":feature:scribble:domain"))
    implementation(project(":feature:insights:domain"))
    implementation(project(":feature:insights:data"))
    implementation(project(":feature:insights:ui"))

    implementation(project(":feature:ledger:ui"))

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

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.ai)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)
}

