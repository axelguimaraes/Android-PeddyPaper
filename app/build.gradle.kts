plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    //id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20"
}

android {
    namespace = "pt.ipp.estg.peddypaper"
    compileSdk = 34

    defaultConfig {
        applicationId = "pt.ipp.estg.peddypaper"
        minSdk = 28
        //noinspection OldTargetApi
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("androidx.media3:media3-common:1.2.0")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // CameraX
    val camaraVersion = "1.3.1"
    implementation("androidx.camera:camera-core:${camaraVersion}")
    implementation("androidx.camera:camera-camera2:${camaraVersion}")
    implementation("androidx.camera:camera-lifecycle:${camaraVersion}")
    implementation("androidx.camera:camera-view:${camaraVersion}")
    implementation("androidx.camera:camera-extensions:${camaraVersion}")

    // Google Maps - Compose
    implementation("com.google.maps.android:maps-compose:4.3.0")
    // Google Maps - Services
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    // Google Maps - Utils
    implementation("com.google.maps.android:android-maps-utils:3.7.0")

    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    //ksp("androidx.room:room-compiler:$roomVersion")
    //noinspection KaptUsageInsteadOfKsp
    kapt("androidx.room:room-compiler:$roomVersion")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.firebaseui:firebase-ui-auth:7.2.0")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // ViewModel dependencies
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    // ML Kit
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("androidx.camera:camera-mlkit-vision:1.4.0-alpha03")
    implementation("com.google.mlkit:text-recognition:16.0.0")
    //implementation("com.google.mlkit:text-recognition-common:16.0.0")

    implementation("com.google.zxing:core:3.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
}