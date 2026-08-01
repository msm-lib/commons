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

val jooqVersion = "3.21.1"
var jacksonVersion = "2.22.1"
var logbackVersion = "1.5.37"
val lombokVersion = "1.18.30"
val guavaVersion = "33.5.0-jre"
val queryDslVersion = "6.10.1"

dependencies {

    implementation("com.google.guava:guava:${guavaVersion}")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${jacksonVersion}")

    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("ch.qos.logback:logback-classic:${logbackVersion}")

    //lombok
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")


    // =======================================================================
    // QueryDsl
    // =======================================================================
    implementation("io.github.openfeign.querydsl:querydsl-core:${queryDslVersion}")
    implementation("io.github.openfeign.querydsl:querydsl-jpa:${queryDslVersion}")
    annotationProcessor("io.github.openfeign.querydsl:querydsl-apt:${queryDslVersion}:jpa")
    // Persistence API processor
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
    // =======================================================================

    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("org.hibernate.validator:hibernate-validator:8.0.2.Final")
    implementation("org.hibernate.orm:hibernate-core:6.6.22.Final")



    // =======================================================================
    // Easy rules
    // =======================================================================
    implementation("org.jeasy:easy-rules-core:4.1.0")
    implementation("org.mvel:mvel2:2.5.2.Final")
    constraints {
        implementation("com.thoughtworks.xstream:xstream:1.4.21") {
            because("Fix RCE of XStream")
        }
        implementation("org.json:json:20240303") {
            because("Fix Denial of Service CVE-2023-5072")
        }
        implementation("org.apache.commons:commons-lang3:3.18.0") {
            because("Fix Uncontrolled Recursion CVE-2025-48924")
        }
    }
    // =======================================================================



    implementation("org.jooq:jooq:${jooqVersion}")
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