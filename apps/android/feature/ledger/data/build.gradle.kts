plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
}

android {
    namespace = "com.scribblefit.feature.ledger.data"
}

dependencies {
    implementation(project(":feature:ledger:domain"))
    implementation(project(":feature:workouts:domain"))
    implementation(project(":core:model"))
}
