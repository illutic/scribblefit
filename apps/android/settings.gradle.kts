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
include(":core:network")
include(":core:designsystem")
include(":core:navigation")
include(":core:coroutines")
include(":core:model")
include(":core:config:domain")
include(":core:config:data")
include(":feature:ai:domain")
include(":feature:ai:data")
include(":feature:scribble:domain")
include(":feature:scribble:data")
include(":feature:exercises:domain")
include(":feature:exercises:data")
include(":feature:workouts:domain")
include(":feature:workouts:data")
include(":feature:sets:domain")
include(":feature:sets:data")
