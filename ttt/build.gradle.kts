plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.lollipop.wear.ttt"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lollipop.wear.ttt"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.1.0"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.core.splashscreen)
    implementation(libs.appcompat)
    implementation(project(":basic"))
    implementation(project(":wearBasic"))
}