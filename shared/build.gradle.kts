plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    kotlin("plugin.serialization") version "1.9.0"
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("app.cash.sqldelight") version "2.0.2"
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val ktorVersion = "2.3.8"
        commonMain.dependencies {
            implementation(libs.timber)
            implementation(libs.kotlinx.coroutines.core.v160nativemt)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.runtime)
            implementation(libs.coroutines.extensions)

        }
        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
            implementation(libs.native.driver)
        }
        androidMain.dependencies{
            implementation(libs.android.driver.v202)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(kotlin("test"))
            implementation(libs.sqlite.driver.v202)
            implementation(libs.sqlite.jdbc)
            implementation(libs.kotlinx.coroutines.test)
        }
    }


    sqldelight {
        databases {
            create("AppDatabase") {
                packageName.set("com.unlam.tpmarvel")
            }
        }
    }

}

android {
    namespace = "com.unlam.tpmarvel"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
dependencies {

    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.firebase.auth.ktx)
}
