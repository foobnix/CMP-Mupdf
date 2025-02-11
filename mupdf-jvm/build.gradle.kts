plugins {
	java
}

tasks.withType<JavaExec> {
	systemProperty("java.library.path", "libs")
}


sourceSets {
	main {
		java {
			srcDir("src/main/java")
		}
	}
}


