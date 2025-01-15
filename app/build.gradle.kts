import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
    alias(libs.plugins.google.gms.google.services)
    id ("kotlin-kapt")
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.gogoviet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gogoviet"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            FileInputStream(localPropertiesFile).use { localProperties.load(it) }
        }

        fun getLocalProperty(key: String): String? = localProperties.getProperty(key)

        buildConfigField("String", "PLACES_API_KEY", "\"${getLocalProperty("PLACES_API_KEY")}\"")
        buildConfigField("String", "ROUTE_API_KEY", "\"${getLocalProperty("ROUTE_API_KEY")}\"")
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.material.icons.core.android)
    implementation(libs.firebase.auth.v2211) // Use the latest version
    implementation(libs.androidx.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)
    implementation(libs.play.services.location)
    implementation(libs.accompanist.permissions)
    implementation(libs.coil.compose)

    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    implementation ("androidx.compose.material:material:1.4.1")


    implementation("androidx.hilt:hilt-navigation-compose:1.1.0-alpha01")
//    implementation ("com.google.accompanist:accompanist-pager:0.28.0")
    implementation ("androidx.compose.foundation:foundation-layout:1.4.1")  // Thêm để sử dụng Pager
    implementation ("androidx.compose.foundation:foundation:1.4.1") // Thêm để sử dụng các thành phần cơ bản trong Compose

    // Phiên bản mới nhất của VerticalPager
//    implementation(libs.hilt.android)
    implementation ("com.google.dagger:hilt-android:2.54")
    kapt ("com.google.dagger:hilt-compiler:2.54")
    implementation ("androidx.media3:media3-exoplayer:1.0.0")
    implementation ("androidx.media3:media3-exoplayer-dash:1.0.0")
    implementation ("androidx.media3:media3-ui:1.0.0")
    implementation ("androidx.media3:media3-exoplayer-hls:1.0.0")

//


    //handle picture
    implementation(libs.kotlinx.serialization.json.v163)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.okhttp)
}
//
kapt {
    correctErrorTypes = true
}
