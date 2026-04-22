plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.unit.test")
}

android {
    namespace = "com.scribblefit.feature.exercises.domain"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":feature:sets:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:workouts:domain"))
    implementation(project(":core:config:domain"))

    implementation(libs.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
}
