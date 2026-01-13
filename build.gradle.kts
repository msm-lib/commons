plugins {
    id("java-library")
}

group = "com.msm"
version = "1.0.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral{
		metadataSources {
			mavenPom()
			artifact()
		}
	}
	mavenCentral()
}

val lombokVersion = "1.18.30"
val guavaVersion = "33.4.8-jre"

dependencies {

//	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("com.google.guava:guava:${guavaVersion}")

	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.4")



    //apache common lang
	implementation("org.apache.commons:commons-lang3:3.18.0")

    //lombok
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

//    implementation("org.slf4j:slf4j-simple:1.7.36")
//	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    implementation("org.apache.commons:commons-collections4:4.5.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
