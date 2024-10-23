plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.lollipop.wear.phone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lollipop.wear.phone"
        minSdk = 25
        targetSdk = 35
        versionCode = 1_00_00
        versionName = "1.0.0"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.appcompat)
    implementation(project(":basic"))
    implementation(project(":wifiP2p"))
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
}