import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        //iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            linkerOpts.add("-Xbinary=bundleId=mobi.librera.mupdf")
        }
    }

    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime.compose)

                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.8.2")

                implementation("io.github.vinceglb:filekit-core:0.8.8")
                implementation("io.github.vinceglb:filekit-compose:0.8.8")




                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.koin.core)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            }
        }
        val mupdfMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("net.java.dev.jna:jna:5.18.1@aar")
            }
        }

        val androidMain by getting {
            dependsOn(mupdfMain)
            dependencies {
                implementation(project(":mupdf-android"))
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)

                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)

            }
        }

        val desktopMain by getting {
            dependsOn(mupdfMain)
            dependencies {
                implementation("net.java.dev.jna:jna:5.15.0")
                implementation(project(":mupdf-jvm"))
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }

//        iosMain.dependencies {
//            implementation(project(":mupdf-ios"))
//        }
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(project(":mupdf-ios"))
            }
        }

        val iosArm64Main by getting {
            dependsOn(iosMain)
        }

        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }

    }
}
tasks.withType<JavaExec> {
    systemProperty("java.library.path", "../mupdf-jvm/libs")
}


android {
    namespace = "mobi.librera.mupdf.demo"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "mobi.librera.mupdf.demo"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "mobi.librera.mupdf.demo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "mobi.librera.mupdf.demo"
            packageVersion = "1.0.0"

            linux {
                modules("jdk.security.auth")
            }
        }
    }
}
