import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.squad.update.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly( libs.android.gradlePlugin )
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register( "androidLibrary" ) {
            id = "update.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register( "androidFlavors" ) {
            id = "update.android.application.flavors"
            implementationClass = "AndroidApplicationFlavorsConventionPlugin"
        }
        register( "hilt" ) {
            id = "update.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register( "androidFeature" ) {
            id = "update.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
    }
}