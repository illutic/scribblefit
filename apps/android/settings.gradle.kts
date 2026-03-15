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
include(
    ":core:database",
    ":core:network",
    ":core:designsystem",
    ":core:navigation",
    ":core:coroutines",
    ":core:model",
    ":core:common",
    ":core:config:domain",
    ":core:config:data"
)
include(":feature:ai:domain", ":feature:ai:data")
include(":feature:scribble:domain", ":feature:scribble:data")
include(":feature:exercises:domain", ":feature:exercises:data")
include(":feature:workouts:domain", ":feature:workouts:data")
include(":feature:sets:domain", ":feature:sets:data")
include(":feature:canvas:ui", ":feature:canvas:domain", ":feature:canvas:data")
