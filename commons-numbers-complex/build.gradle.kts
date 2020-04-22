plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

group = "org.apache.commonskt.numbers"
description = "Port of org.apache.commons.numbers.complex"

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
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.apache.commonskt:kotlin-stdlib-extension-jvm:1.0.1")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("org.apache.commonskt:kotlin-stdlib-extension-js:1.0.1")
            }
        }
        val mingwX64Main by getting {
            dependencies {
                implementation("org.apache.commonskt:kotlin-stdlib-extension-mingwx64:1.0.1")
            }
        }
        val linuxX64Main by getting {
            dependencies {
                implementation("org.apache.commonskt:kotlin-stdlib-extension-linuxx64:1.0.1")
            }
        }
    }
}