plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
}

android {
    namespace = "com.scribblefit.feature.sets.data"
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:coroutines"))
    implementation(project(":core:model"))
    implementation(project(":feature:sets:domain"))
}
