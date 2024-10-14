plugins {
    alias( libs.plugins.update.android.library )
    alias( libs.plugins.update.hilt )
    alias( libs.plugins.jetbrains.kotlin.android )
}

android {
    namespace = "com.squad.update.sync"
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
}

dependencies {
    ksp( libs.hilt.ext.compiler )

    implementation( libs.androidx.work.ktx )
    implementation( libs.hilt.ext.work )

    implementation( projects.core.data )
    implementation( projects.core.datastore )
    implementation( projects.core.common )

    androidTestImplementation( libs.androidx.work.testing )
    androidTestImplementation( libs.hilt.android.testing )

    androidTestImplementation( libs.kotlinx.coroutines.guava )
    androidTestImplementation( projects.core.testing )
}