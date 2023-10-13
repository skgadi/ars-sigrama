plugins {
    id("com.android.application")
}

android {
    namespace = "mx.com.sigrama.ars"
    compileSdk = 34
    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "mx.com.sigrama.ars"
        minSdk = 24
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
    androidResources {
        generateLocaleConfig = true //added for per-app language support
    }
}

dependencies {

    //The following code taken from
    // https://developer.android.com/jetpack/androidx/releases/core
    // Initially used for window in WindowCompat
    val core_version = "1.12.0"
    // Java language implementation
    implementation("androidx.core:core:$core_version")
    // Kotlin
    implementation("androidx.core:core-ktx:$core_version")
    // To use RoleManagerCompat
    implementation("androidx.core:core-role:1.0.0")
    // To use the Animator APIs
    implementation("androidx.core:core-animation:1.0.0-rc01")
    // To test the Animator APIs
    androidTestImplementation("androidx.core:core-animation-testing:1.0.0-rc01")
    // Optional - To enable APIs that query the performance characteristics of GMS devices.
    implementation("androidx.core:core-performance:1.0.0-beta02")
    // Optional - to use ShortcutManagerCompat to donate shortcuts to be used by Google
    implementation("androidx.core:core-google-shortcuts:1.1.0")
    // Optional - to support backwards compatibility of RemoteViews
    implementation("androidx.core:core-remoteviews:1.0.0")
    // Optional - APIs for SplashScreen, including compatibility helpers on devices prior Android 12
    implementation("androidx.core:core-splashscreen:1.1.0-alpha02")



    // available by default
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // supporting to slide between pages.
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    // supporting to use MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //animation of ViewPager2 transformation
    implementation ("com.github.CodeBoy722:ViewPager2_Transformers:1.0.0")

    //Implementation of QR code scanner
    //implementation ("com.budiyev.android:code-scanner:2.1.0")
    implementation ("com.google.android.gms:play-services-code-scanner:16.1.0")



}