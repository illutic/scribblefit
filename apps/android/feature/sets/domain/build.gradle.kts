plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.unit.test")
}

android {
    namespace = "com.scribblefit.feature.sets.domain"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
}
