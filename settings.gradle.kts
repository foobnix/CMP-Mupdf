rootProject.name = "CMP-Mupdf"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenCentral()
    }
}

include(":composeApp")
include(":mupdf-jvm")
include(":mupdf-android")
include(":mupdf-ios")

//include(":mupdf-jvm-binding")
//include(":mupdf-wasm")