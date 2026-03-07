plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
}

android {
    namespace = "com.scribblefit.feature.profile.data"
}

dependencies {
    implementation(project(":feature:profile:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:ai:data"))
    implementation(project(":feature:ledger"))
    implementation(project(":core:database"))

    implementation(libs.hilt.android)
}
