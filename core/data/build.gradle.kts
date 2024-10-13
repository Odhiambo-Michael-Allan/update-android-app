plugins {
    alias( libs.plugins.android.library )
    alias( libs.plugins.jetbrains.kotlin.android )

    alias( libs.plugins.update.android.library )

    alias( libs.plugins.hilt )
    alias( libs.plugins.ksp )
}

android {
    namespace = "com.squad.update.core.data"
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
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {

    implementation( projects.core.model )
    implementation( projects.core.datastore )

    implementation( projects.core.database )
    implementation( projects.core.network )

    implementation( libs.androidx.core.ktx )
    implementation( libs.kotlinx.datetime )

    implementation( libs.hilt.android )
    implementation( libs.hilt.core )
    ksp( libs.hilt.compiler )

    testImplementation( libs.junit )
    testImplementation( libs.kotlinx.coroutines.test )

    testImplementation( libs.kotlinx.serialization.json )
    testImplementation( projects.core.datastoreTest )

    testImplementation( projects.core.testing )
}