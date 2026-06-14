plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.canvas.domain"

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:coroutines"))
    implementation(project(":core:common"))
    implementation(project(":feature:scribble:domain"))
    implementation(project(":feature:exercises:domain"))
    implementation(project(":feature:sets:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(libs.slf4j.android)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.junit)
}
