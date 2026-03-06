plugins {
    id("scribblefit.android.feature")
}

android {
    namespace = "com.scribblefit.feature.ledger"
}

dependencies {
    implementation(project(":feature:ai:domain"))
}
