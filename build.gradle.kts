// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlinVersion = "1.5.0"
    repositories {
        google()
//        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
//        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.15.0")
//        classpath("com.novoda:bintray-release:0.9")
//        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
//        jcenter()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io") }
    }
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}