plugins {
    alias( libs.plugins.jetbrains.kotlin.android )

    alias( libs.plugins.update.android.library )

    alias( libs.plugins.ksp )
}

android {
    namespace = "com.squad.update.core.domain"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation( projects.core.data )
    implementation( projects.core.model )
    
    implementation( libs.androidx.core.ktx )
    implementation( libs.androidx.appcompat )

    implementation( libs.hilt.core )
    ksp( libs.hilt.compiler )

    testImplementation( libs.junit )
    testImplementation( projects.core.testing )
}