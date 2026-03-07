pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "scribblefit-android"

includeBuild("build-logic")

include(":app")
include(":core:database")
include(":core:model")
include(":core:network")
include(":core:designsystem")
include(":feature:ai:domain")
include(":feature:ai:data")
include(":feature:canvas")
include(":feature:ledger")
