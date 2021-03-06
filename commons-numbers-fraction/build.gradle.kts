import java.util.Properties

plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("com.jfrog.bintray")
}

description = "Port of org.apache.commons.numbers.fraction"

val kotlinStdlibExtensionGroup = "com.github.alexandrelombard.commonskt"
val kotlinStdlibExtensionVersion = "1.0.3"

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
                implementation("$kotlinStdlibExtensionGroup:kotlin-stdlib-extension-metadata:$kotlinStdlibExtensionVersion")
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
                implementation("$kotlinStdlibExtensionGroup:kotlin-stdlib-extension-jvm:$kotlinStdlibExtensionVersion")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("$kotlinStdlibExtensionGroup:kotlin-stdlib-extension-js:$kotlinStdlibExtensionVersion")
            }
        }
    }
}

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())

bintray {
    user = localProperties.getProperty("bintray.user")
    key = localProperties.getProperty("bintray.apikey")
    dryRun = false
    publish = true
    override = true
    val pubs = publishing.publications
        .map { it.name }
        .filter { it != "kotlinMultiplatform" }
        .toTypedArray()
    setPublications(*pubs)
    pkg(delegateClosureOf<com.jfrog.bintray.gradle.BintrayExtension.PackageConfig> {
        repo = "maven"
        name = project.name
        desc = project.description
        websiteUrl = "https://github.com/alexandrelombard/commonskt-numbers"
        vcsUrl = "https://github.com/alexandrelombard/commonskt-numbers.git"
        version.vcsTag = "v${project.version}"
        setLicenses("Apache-2.0")
        publicDownloadNumbers = true
    })
}