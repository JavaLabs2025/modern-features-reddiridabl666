plugins {
    id("java")
    id("application")

    id("io.freefair.lombok") version "9.1.0"
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
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
