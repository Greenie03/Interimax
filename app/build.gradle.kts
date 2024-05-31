plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.interimax"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.example.interimax"
        minSdk = 29
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    implementation(libs.play.services.maps)


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.runtime)
    implementation(libs.play.services.location)
    implementation(libs.com.google.gms.google.services.gradle.plugin)
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation(libs.recyclerview)
    implementation(libs.adapters)
    implementation(libs.databinding.runtime)
    implementation(libs.databinding.common)
    implementation(libs.databinding.adapters)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.android.material:material:1.12.0")

    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))

}