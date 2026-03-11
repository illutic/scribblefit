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
include(":core:config:domain")
include(":core:config:data")
include(":feature:workout:domain")
include(":feature:workout:data")
include(":feature:ai:domain")
include(":feature:ai:data")
include(":feature:scribble:domain")
include(":feature:scribble:data")
include(":feature:canvas:domain")
include(":feature:canvas:data")
include(":feature:canvas:ui")
include(":feature:ledger")
include(":feature:analytics:domain")
include(":feature:analytics:data")
include(":feature:profile:domain")
include(":feature:profile:data")
include(":feature:profile:ui")
