import java.util.Properties

plugins {
    id("base")
    kotlin("multiplatform") version "1.3.72"
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.5"
}

repositories {
    mavenLocal()
    mavenCentral()
}

allprojects {
    group = "org.apache.commonskt.numbers"
    version = "1.0.0-SNAPSHOT"

    repositories {
        jcenter()
    }
}

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())

subprojects {
    val subproject = this@subprojects

    apply(plugin = "maven-publish")
    apply(plugin = "com.jfrog.bintray")

    bintray {
        user = localProperties.getProperty("bintray.user")
        key = localProperties.getProperty("bintray.apikey")
        dryRun = false
        publish = true
        val pubs = publishing.publications
            .map { it.name }
            .filter { it != "kotlinMultiplatform" }
            .toTypedArray()
        setPublications(*pubs)
        pkg(delegateClosureOf<com.jfrog.bintray.gradle.BintrayExtension.PackageConfig> {
            repo = "maven"
            name = subproject.name
            desc = project.description
            githubRepo = "https://github.com/alexandrelombard/commonskt-numbers"
            websiteUrl = "https://github.com/alexandrelombard/commonskt-numbers"
            vcsUrl = "https://github.com/alexandrelombard/commonskt-numbers.git"
            version.vcsTag = "v${project.version}"
            setLicenses("Apache-2.0")
            publicDownloadNumbers = true
        })
    }
}