plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

group = "org.apache.commonskt.numbers"
description = "Port of org.apache.commons.numbers.combinatorics"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    jvm()
    js()
    mingwX64()
    linuxX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.apache.commonskt:kotlin-stdlib-extension-metadata:1.0-SNAPSHOT")
                implementation(project(":commons-numbers-core"))
                implementation(project(":commons-numbers-gamma"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.apache.commonskt:kotlin-stdlib-extension-jvm:1.0-SNAPSHOT")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("org.apache.commonskt:kotlin-stdlib-extension-js:1.0-SNAPSHOT")
            }
        }
    }
}