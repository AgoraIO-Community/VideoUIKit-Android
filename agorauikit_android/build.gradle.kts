plugins {
    id("maven-publish")
//    id("maven")
    id("com.android.library")
    id("kotlin-android")
    id("org.jetbrains.dokka") version "1.4.32"
    kotlin("plugin.serialization") version "1.4.10"
}
// group = "com.github.agoraio-community"

android {
    compileSdkVersion(31)
    buildToolsVersion("30.0.2")

    defaultConfig {
        minSdk = 24
        targetSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles("proguard-rules.pro")
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

// Because the components are created only during the afterEvaluate phase, you must
// configure your publications using the afterEvaluate() lifecycle method.
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                // Applies the component for the release build variant.
                from(components["release"])
                groupId = "com.github.agoraio-community"
                artifactId = "final"
                version = "2.0.6"
            }
            // Creates a Maven publication called “debug”.
            create<MavenPublication>("debug") {
                // Applies the component for the debug build variant.
                from(components["debug"])
                groupId = "com.github.agoraio-community"
                artifactId = "final-debug"
                version = "2.0.6"
            }
        }
    }
}

dependencies {
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.android.material:material:1.6.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    api("io.agora.rtc:full-sdk:4.0.1")
    api("io.agora.rtm:rtm-sdk:1.5.3")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.4.32")
}
