plugins {
    id("java")
    id("application")

    id("io.freefair.lombok") version "9.1.0"

    id("jacoco")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(25))
  }
}

application {
    mainClass = "org.lab.Main"
}

group = "org.lab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
    testImplementation("org.assertj:assertj-core:3.27.6")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")

        setExceptionFormat("full")
    }

    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}
