plugins {
    id("scribblefit.android.library.compose")
    id("scribblefit.android.room")
    id("scribblefit.android.hilt")
}

android {
    namespace = "com.scribblefit.core.database"
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:config:domain"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
}
