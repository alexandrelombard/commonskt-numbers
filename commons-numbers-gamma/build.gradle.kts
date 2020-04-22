plugins {
    kotlin("multiplatform")
}

group = "org.apache.commonskt.numbers"
description = "Port of org.apache.commons.numbers.gamma"

repositories {
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
                implementation(project(":commons-numbers-fraction"))
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
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
    }
}