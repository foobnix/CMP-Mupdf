plugins {
	alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
	js {
		browser {
			binaries.executable()
		}
	}

	wasmJs {
		browser {
			binaries.executable()
		}
	}

	sourceSets {
		val jsMain by getting {
			dependencies {
				// Add any JS-specific dependencies here
			}
			resources.srcDir("libs")
		}
		val wasmJsMain by getting {
			dependencies {
				// Add any Wasm-specific dependencies here
			}
			resources.srcDir("libs")
		}
	}
}



