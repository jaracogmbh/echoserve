
plugins {
    java
    application
    id("com.avast.gradle.docker-compose") version "0.17.8"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    kotlin("jvm") version "2.0.0"
}

group = "de.jaraco"
version = ""


java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}
repositories {
    mavenCentral()
}

dependencies {
    implementation("org.wiremock:wiremock-standalone:3.9.1")
    implementation("io.ktor:ktor-server-core-jvm:2.3.12")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.12")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.12")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.12")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.withType<Jar> {
  manifest {
    attributes["Main-Class"] = "de.jaraco.MainKt"
  }
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  from(sourceSets.main.get().output)
  excludes.add("META-INF/*.SF")
  excludes.add("META-INF/*.DSA")
  excludes.add("META-INF/*.RSA")
  dependsOn(configurations.runtimeClasspath)
  from({
    configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
  })
    archiveBaseName.set("application")
}

