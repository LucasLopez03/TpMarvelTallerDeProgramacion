plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "1.9.0"
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("app.cash.sqldelight") version "2.0.2"
}

android {
    namespace = "com.unlam.tpmarvel.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.unlam.tpmarvel.android"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
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
    implementation(libs.android.driver.v202)
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.appcompat)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.timber)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.material.v190)
    implementation(libs.picasso)
    implementation(libs.material)
}