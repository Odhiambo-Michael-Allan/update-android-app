import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.ProductFlavor

plugins {
    alias( libs.plugins.android.library )
    alias( libs.plugins.update.android.library )

    alias( libs.plugins.jetbrains.kotlin.android )
    alias( libs.plugins.kotlin.serialization )

    alias( libs.plugins.hilt )
    alias( libs.plugins.ksp )
}

android {
    namespace = "com.squad.update.core.network"
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
    buildFeatures {
        buildConfig = true
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {

    implementation( projects.core.common )
    implementation( projects.core.model )

    implementation( libs.kotlinx.datetime )
    implementation( libs.coil.kt )

    implementation( libs.coil.kt.svg )
    implementation( libs.kotlinx.serialization.json )

    implementation( libs.okhttp.logging )
    implementation( libs.retrofit.core )

    implementation( libs.retrofit.kotlin.serialization )

    implementation( libs.hilt.core )
    implementation( libs.hilt.android )
    ksp( libs.hilt.compiler )

    testImplementation( libs.junit )
    testImplementation( libs.kotlinx.coroutines.test )

}




