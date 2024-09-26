plugins {
    alias( libs.plugins.android.library )
    alias( libs.plugins.jetbrains.kotlin.android )

    alias( libs.plugins.compose )
//    alias( libs.plugins.hilt )

    alias( libs.plugins.ksp )
}

android {
    namespace = "com.squad.update.feature.foryou"
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

    implementation( projects.core.data )
    implementation( projects.core.domain )

    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.appcompat )

    implementation( platform( libs.androidx.compose.bom ) )
    implementation( libs.androidx.material3 )

    implementation( libs.androidx.navigation.compose )
    implementation( libs.androidx.hilt.navigation.compose )

//    implementation( libs.hilt.android )
    implementation( libs.hilt.core )
    ksp( libs.hilt.compiler )

    testImplementation( libs.junit )
    androidTestImplementation( libs.androidx.junit )
    androidTestImplementation( libs.androidx.espresso.core )
}