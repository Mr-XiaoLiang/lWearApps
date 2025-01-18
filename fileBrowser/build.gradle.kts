plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.lollipop.filebrowser"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.lollipop.filebrowser"
        minSdk = 28
        targetSdk = 34
        versionCode = 1_00_00
        versionName = "1.0.0"
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
}

dependencies {
    implementation(libs.core.splashscreen)
    implementation(libs.appcompat)
    implementation(libs.material)
//    implementation(libs.activity)
//    implementation(libs.constraintlayout)
    implementation(project(":basic"))
    implementation(project(":wearBasic"))
    implementation(project(":qrView"))
    implementation(project(":swiftp"))
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation ("com.github.bumptech.glide:glide:4.15.0")
}