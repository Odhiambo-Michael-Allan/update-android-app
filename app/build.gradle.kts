plugins {
    alias( libs.plugins.android.application )
    alias( libs.plugins.jetbrains.kotlin.android )

    alias( libs.plugins.compose )
    alias( libs.plugins.hilt )

    alias( libs.plugins.ksp )
}

android {
    namespace = "com.squad.update"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.squad.update"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation( projects.core.data )
    implementation( projects.core.model )

    implementation( projects.core.designsystem )
    implementation( projects.feature.foryou )

    implementation( projects.core.ui )

    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.lifecycle.runtime.ktx )

    implementation( libs.androidx.activity.compose )
    implementation( platform( libs.androidx.compose.bom ) )

    implementation( libs.androidx.ui )
    implementation( libs.androidx.ui.graphics )

    implementation( libs.androidx.ui.tooling.preview )
    implementation( libs.androidx.material3 )

    implementation( libs.hilt.android )
    implementation( libs.hilt.core )
    ksp( libs.hilt.compiler )

    implementation( libs.androidx.core.splashscreen )
    implementation( libs.androidx.hilt.navigation.compose )

    implementation( libs.androidx.navigation.compose )
    implementation( libs.kotlinx.datetime )

    implementation( libs.androidx.compose.material3.adaptive )

    testImplementation( libs.junit )

    androidTestImplementation( libs.androidx.junit )

    androidTestImplementation( libs.androidx.espresso.core )
    androidTestImplementation( platform( libs.androidx.compose.bom ) )

    androidTestImplementation( libs.androidx.ui.test.junit4 )
    debugImplementation( libs.androidx.ui.tooling )

    debugImplementation( libs.androidx.ui.test.manifest )
}