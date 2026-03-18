plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
}

android {
    namespace = "com.scribblefit.feature.insights.data"
}

dependencies {
    implementation(project(":feature:insights:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:coroutines"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
