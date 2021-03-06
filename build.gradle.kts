import java.util.Properties

plugins {
    id("base")
    kotlin("multiplatform") version "1.3.72"
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.3"
}

repositories {
    mavenLocal()
    mavenCentral()
}

allprojects {
    group = "com.github.alexandrelombard.commonskt.numbers"
    version = "1.0.3"

    repositories {
        jcenter()
        maven {
            url = uri("https://dl.bintray.com/alexandrelombard/maven")
        }
    }
}