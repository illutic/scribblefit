plugins {
    id("scribblefit.android.library.compose")
    id("scribblefit.android.hilt")
    id("scribblefit.android.unit.test")
}

android {
    namespace = "com.scribblefit.feature.ledger.ui"
}

dependencies {
    implementation(project(":feature:ledger:domain"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.viewmodel.compose)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
