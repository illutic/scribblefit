plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.ai.domain"
}

dependencies {
    implementation(project(":core:ai"))
    implementation(project(":feature:analytics:domain"))
    implementation(project(":feature:canvas:domain"))
    implementation(project(":core:model"))
    
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
}
