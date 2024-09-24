plugins {
    alias( libs.plugins.android.library )
    alias( libs.plugins.jetbrains.kotlin.android )
    alias( libs.plugins.compose )
}

android {
    namespace = "com.squad.update.core.designsystem"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {

    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.appcompat )

    implementation( platform( libs.androidx.compose.bom ) )
    implementation( libs.androidx.compose.runtime )

    implementation( libs.androidx.compose.material3 )
    implementation( libs.androidx.compose.material.iconsExtended )

    implementation( libs.androidx.compose.ui.tooling )
    implementation( libs.androidx.compose.material3.adaptive )

    implementation( libs.androidx.compose.material3.navigationSuite )

    testImplementation( libs.junit )

    androidTestImplementation( libs.androidx.junit )
    androidTestImplementation( libs.androidx.espresso.core )
}