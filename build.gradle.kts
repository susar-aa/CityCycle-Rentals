plugins {
    id ("com.android.application") version ("8.8.0") apply false
    // You can add other plugin aliases here if needed
}

buildscript {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven ("https://jitpack.io")
    }

    dependencies {
        // Add the Android Gradle Plugin classpath here
        classpath ("com.android.tools.build:gradle:8.8.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven ("https://jitpack.io")
    }
}