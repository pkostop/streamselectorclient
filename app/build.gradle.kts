plugins {
    alias(libs.plugins.androidApplication)
}


android {
    namespace = "org.kemea.isafeco.client"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.kemea.isafeco.client"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation("org.nanohttpd:nanohttpd:2.3.1")

    implementation("androidx.camera:camera-core:1.2.2")
    implementation("androidx.camera:camera-camera2:1.2.2")
    implementation("androidx.camera:camera-lifecycle:1.2.2")
    implementation("androidx.camera:camera-video:1.2.2")

    implementation("androidx.camera:camera-view:1.2.2")
    implementation("androidx.camera:camera-extensions:1.2.2")

    implementation("com.arthenica:ffmpeg-kit-full:5.1")

    implementation("com.google.code.gson:gson:2.11.0")
    //implementation ("org.videolan.android:libvlc-all:3.6.0")

    //implementation("libs.media3.exoplayer")

    implementation ("androidx.media3:media3-exoplayer:1.4.1") // ExoPlayer core library
    implementation ("androidx.media3:media3-exoplayer-rtsp:1.4.1")
    implementation ("androidx.media3:media3-ui:1.4.1")
    implementation(libs.firebase.inappmessaging)       // For PlayerView

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

