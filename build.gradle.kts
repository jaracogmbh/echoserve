
plugins {
    java
    application
    id("com.avast.gradle.docker-compose") version "0.17.15"
    kotlin("jvm") version "2.1.20"
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
    implementation("org.wiremock:wiremock-standalone:3.12.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.withType<Jar>() {
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

