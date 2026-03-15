plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.sets.domain"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
