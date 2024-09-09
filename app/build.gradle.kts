plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.dagger.hilt.android") version "2.51.1" apply true
    id("kotlin-kapt")
}
val hilt_version = "2.51.1"
android {
    namespace = "com.amorgens"
    compileSdk = 34

    flavorDimensions +="environment"
    productFlavors {

        create("prod") {
            dimension = "environment"
            // Additional configurations for this flavor
            buildConfigField("String", "API_URL", "\"http://155.138.224.183:8000\"")
            buildConfigField("String", "BLOCKCHAIN_URL", "\"155.138.224.183:3000\"")
        }
        create("dev") {
            dimension = "environment"
            // Additional configurations for this flavor
            applicationIdSuffix = ".dev"
            buildConfigField("String", "API_URL", "\"http://155.138.224.183:8000\"")
            buildConfigField("String", "BLOCKCHAIN_URL", "\"155.138.224.183:3000\"")

        }
    }
    defaultConfig {
        applicationId = "com.amorgens"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {

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
    implementation(libs.androidx.navigation.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // material 3
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.3.0-beta03")
    implementation("androidx.compose.material:material-icons-extended:1.6.8")

    // hilt
    implementation ("com.google.dagger:hilt-android:$hilt_version")
    kapt ("com.google.dagger:hilt-compiler:$hilt_version")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")
    kapt ("com.google.dagger:hilt-android-compiler:2.48")

    // coroutine
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")


    // gson
    implementation("com.google.code.gson:gson:2.10.1")

    //lotie animation

    implementation("com.airbnb.android:lottie-compose:4.0.0")

    // room db
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    // To use Kotlin Symbol Processing (KSP)
    //ksp("androidx.room:room-compiler:$room_version")


    // retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // crypto shared
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")

    // sentry
    implementation (libs.sentry.android)

}

kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = false
}