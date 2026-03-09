plugins {
    id("java-library")
    id("maven-publish")
}

group = "com.msm"
version = "1.0.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
    withSourcesJar()
    withJavadocJar()
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

    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("ch.qos.logback:logback-classic:1.4.14")


    //apache common lang
//	implementation("org.apache.commons:commons-lang3:3.18.0")

    //lombok
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    implementation("org.apache.commons:commons-collections4:4.5.0")
    implementation("com.querydsl:querydsl-core:5.1.0")
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")
//    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("org.hibernate.validator:hibernate-validator:8.0.2.Final")
    implementation("org.hibernate.orm:hibernate-core:6.6.22.Final")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "commons"
            version = project.version.toString()
        }
    }
}