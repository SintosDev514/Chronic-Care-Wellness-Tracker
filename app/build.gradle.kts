plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // The kotlin.compose plugin is usually applied by the Android Gradle Plugin,
    // so this alias might not be necessary, but it doesn't harm.
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.chronicare"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.chronicare"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        compose = true
    }

    // This ensures your Compose compiler is compatible with your Kotlin version
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Or match the version compatible with your Kotlin plugin
    }
}

dependencies {
    // Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Firebase - Import the Bill of Materials (BoM)
    implementation(platform("com.google.firebase:firebase-bom:33.1.2")) // Assumes a recent BoM version
    // Now declare the Auth dependency without a version
    implementation("com.google.firebase:firebase-auth-ktx")

    // Compose - Import the Bill of Materials (BoM)
    implementation(platform("androidx.compose:compose-bom:2024.06.00")) // Updated to a stable 2024 version
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7") // Using the latest stable version

    // Coil for Image Loading
    implementation("io.coil-kt:coil-compose:2.6.0") // Using the latest stable version

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Debug dependencies
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
