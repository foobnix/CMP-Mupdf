plugins {
    alias(libs.plugins.androidLibrary)
}


android {
    namespace = "mobi.librera.libmupdf"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    dependencies {
        implementation(libs.kotlin.test)
        //androidTestImplementation(libs.testng)
    }
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
            java.srcDirs("src/main/java")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}



