pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
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
include(":core:testing")
include(":core:domain")
include(":feature:following")
include(":core:datastore-proto")
include(":core:datastore-test")
include(":core:common")
include(":core:database")
include(":core:network")
include(":sync:sync-test")
include(":sync:work")
