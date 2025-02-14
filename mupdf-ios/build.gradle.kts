plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    listOf(
        //macosArm64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->

        iosTarget.compilations.named("main") {
            cinterops {
                val libmupdf by creating
            }
        }
        //iosTarget.binaries.executable {
            //linkerOpts("-ios_simulator_version_min 11.0")
        //}
    }
}
