plugins {
    id("com.github.dcendents.android-maven")
    id("com.android.library")
    id("kotlin-android")
    id("org.jetbrains.dokka") version "1.4.32"
}
//group = "com.github.agoraio-community"

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.2")

    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles("proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility =  JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.recyclerview:recyclerview:1.2.0")
    val kotlinVersion = "1.5.0"
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("androidx.core:core-ktx:1.5.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    api("com.github.agorabuilder:native-full-sdk:3.4.2")
    implementation("com.squareup.okhttp3:okhttp:3.14.6")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.4.32")
}