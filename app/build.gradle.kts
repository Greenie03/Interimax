plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.interimax"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.example.interimax"
        minSdk = 33
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
    implementation(libs.recyclerview)
    implementation(libs.databinding.runtime)
    implementation(libs.databinding.common)
    implementation(libs.databinding.adapters)
    implementation(libs.firebase.storage)
    implementation(libs.play.services.cast.framework)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.firebase.bom))
    /*implementation("com.google.firebase:firebase-analytics"){
        exclude("com.android.support", "support-v4")
    }*/
    implementation("com.google.firebase:firebase-auth") {
        exclude("com.android.support", "support-v4")

    }
    implementation("com.google.firebase:firebase-firestore") {
        exclude("com.android.support", "support-v4")
    }
    implementation(libs.material)

    implementation(libs.glide)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-storage:21.0.0")
    //implementation("com.github.dhaval2404:imagepicker:2.1")

    implementation("com.github.yalantis:ucrop:2.2.8")


    implementation("de.hdodenhof:circleimageview:3.1.0")
}