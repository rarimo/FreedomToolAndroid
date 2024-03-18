plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")

}

android {
    namespace = "org.freedomtool"
    compileSdk = 34


    defaultConfig {
        applicationId = "org.freedomtool"
        minSdk = 27
        targetSdk = 34
        versionCode = 8
        versionName = "1.1.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    defaultConfig {
        this.resConfigs("en", "ru")
        externalNativeBuild {
            cmake {
                cppFlags += "-fexceptions -frtti -std=c++11"
                arguments += "-DANDROID_STL=c++_shared"
            }
            ndk {
                abiFilters += "arm64-v8a"
            }
        }
    }

    buildTypes {

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
        viewBinding = true
        dataBinding = true
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation(files("libs/Identity.aar"))
    implementation("androidx.preference:preference:1.2.0")
    testImplementation("junit:junit:4.13.2")
    implementation("io.reactivex.rxjava2:rxjava:2.2.9")
    implementation("io.reactivex.rxjava2:rxkotlin:2.0.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.github.TOrnelas:SegmentedProgressBar:0.0.3")
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta02")
    implementation("com.github.addisonelliott:SegmentedButton:3.1.9")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//0.7.40

    implementation("io.noties.markwon:core:4.6.2")

    implementation("io.noties.markwon:ext-strikethrough:4.6.2")
    implementation("io.noties.markwon:ext-tables:4.6.2")
    implementation("io.noties.markwon:ext-tasklist:4.6.2")
    implementation("io.noties.markwon:image:4.6.2")
    implementation("io.noties.markwon:inline-parser:4.6.2")
    implementation("io.noties.markwon:linkify:4.6.2")

    implementation("org.jmrtd:jmrtd:0.7.27")
    implementation("net.sf.scuba:scuba-sc-android:0.0.20")
    implementation("com.github.mhshams:jnbis:1.1.0")
    implementation("com.gemalto.jp2:jp2-android:1.0.3")
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    implementation("org.web3j:core:4.8.8-android")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")

    implementation("com.google.mlkit:camera:16.0.0-beta3")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.28")
    implementation("com.google.android.gms:play-services-vision:20.1.3")
    implementation("com.airbnb.android:lottie:6.3.0")
    implementation("com.google.dagger:dagger:2.28.3")
    kapt("com.google.dagger:dagger-compiler:2.13")
    kapt("com.google.dagger:dagger-android-processor:2.11")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("androidx.biometric:biometric:1.1.0")

}