plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.toolsrus"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.5")
	implementation("org.springframework.boot:spring-boot-starter:3.3.5")
	implementation("org.springframework.boot:spring-boot-starter-web:3.3.5")
	implementation("org.apache.logging.log4j:log4j-core:2.24.1")
	implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")

	compileOnly("org.projectlombok:lombok:1.18.34")

	runtimeOnly("com.h2database:h2:2.3.232")

	annotationProcessor("org.projectlombok:lombok:1.18.34")

	testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.5")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
	testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
	testImplementation("org.mockito:mockito-core:5.14.2")
	testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
	testImplementation("org.junit.platform:junit-platform-engine:1.11.3")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.3")
	testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.11.3")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
