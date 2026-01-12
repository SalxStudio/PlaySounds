plugins {
    kotlin("jvm") version "1.9.22"
    id("com.gradleup.shadow") version "8.3.0"
}

group = "com.salx"
version = "1.3"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/repository/placeholderapi/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    compileJava {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
    shadowJar {
        archiveClassifier.set("")
        relocate("kotlin", "com.salx.playsound.libs.kotlin")
    }
}
