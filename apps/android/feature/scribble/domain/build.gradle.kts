plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.scribble.domain"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":feature:workouts:domain"))
    implementation(project(":feature:exercises:domain"))

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
