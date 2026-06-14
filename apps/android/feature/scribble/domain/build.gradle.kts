plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.unit.test")
}

android {
    namespace = "com.scribblefit.feature.scribble.domain"
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    implementation(project(":core:model"))
    implementation(project(":core:common"))
}
