plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.unit.test")
}

android {
    namespace = "com.scribblefit.feature.exercises.domain"
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":feature:sets:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:scribble:domain"))
    implementation(project(":core:config:domain"))

    implementation(libs.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
}
