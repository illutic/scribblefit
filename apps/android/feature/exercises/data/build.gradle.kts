plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
}

android {
    namespace = "com.scribblefit.feature.exercises.data"
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:coroutines"))
    implementation(project(":core:model"))
    implementation(project(":feature:exercises:domain"))
    implementation(libs.coroutines.android)
}
