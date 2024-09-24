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

rootProject.name = "Update"

enableFeaturePreview( "TYPESAFE_PROJECT_ACCESSORS" )

include( ":app" )
include( ":core:data" )
include( ":core:model" )
include(":core:designsystem")
include(":feature:foryou")
include(":core:datastore")
include(":core:ui")
