plugins {
    kotlin("multiplatform")
}

group = "org.apache.commonskt"
version = "1.0-SNAPSHOT"

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