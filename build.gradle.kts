import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.8.22"
}

group = "com.github.esslerc"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	//implementation("org.postgresql:postgresql")

	implementation("info.picocli:picocli:4.7.5")
	implementation("commons-io:commons-io:2.15.0")

	implementation("org.slf4j:slf4j-api:2.0.9")
	implementation("ch.qos.logback:logback-classic:1.4.11")

	implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.15.3")

	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}



tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
