// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
//        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.3.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.7.20")
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
        mavenCentral()
        gradlePluginPortal()
        maven {
            setUrl("https://jitpack.io")
        }
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
