plugins {
	java
}

tasks.withType<JavaExec> {
	systemProperty("java.library.path", "libs")
}

dependencies{

}

sourceSets {
	main {
		java {
			srcDir("src/main/java")
		}
	}
}


