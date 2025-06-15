import java.io.FileNotFoundException
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kapt)
}

android {
    namespace = "com.example.currencytask"
    compileSdk = 35



    defaultConfig {
        applicationId = "com.example.currencytask"
        minSdk = 22
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // return empty key in case something goes wrong
        try {
            val keystoreFile = project.rootProject.file("apikey.properties")
            val properties = Properties()
            properties.load(keystoreFile.inputStream())

            val apiKey = properties.getProperty("FIXER_API_KEY") ?: ""
            buildConfigField(
                type = "String",
                name = "FIXER_API_KEY",
                value = apiKey,
            )

        } catch (e: FileNotFoundException) {
            println("GRADLE:EXCEPTION ${e.message}")
            buildConfigField(
                type = "String",
                name = "FIXER_API_KEY",
                value = "\"EMPTY\"",
            )
        }
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
        dataBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.recyclerview)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.serialization.json)
    implementation(libs.serialization.converter)
    implementation(libs.ssp)
    implementation(libs.sdp)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.room.runtime)
    androidTestImplementation(libs.room.testing)
    implementation(libs.retrofit)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.datastore.preferences)
}