plugins {
    id("scribblefit.android.feature")
}

android {
    namespace = "com.scribblefit.feature.profile.ui"
}

dependencies {
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:profile:domain"))
    implementation(project(":feature:profile:data"))
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.hilt.navigation.compose)
}
