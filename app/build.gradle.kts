plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.amtehan"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.amtehan"
        minSdk = 31
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true // <--- این خط را اضافه کنید
    }
}

dependencies {


        implementation("com.airbnb.android:lottie:6.1.0")

        implementation("org.osmdroid:osmdroid-android:6.1.16")

        implementation("androidx.cardview:cardview:1.0.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0") // یا هر نسخه دیگری که استفاده می‌کنید


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}