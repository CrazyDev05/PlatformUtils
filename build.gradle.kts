import com.vanniktech.maven.publish.SonatypeHost

plugins {
    java
    alias(libs.plugins.publish)
    alias(libs.plugins.licenser)
    alias(libs.plugins.dokka)
}

group = "de.crazydev22"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.spigot)
    implementation(project(":api"))
    implementation(project(":platform:spigot"))
    implementation(project(":platform:paper"))
}

allprojects {
    apply(plugin = "java")

    java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:26.0.2")
    }
}

dokka.dokkaSourceSets.javaMain {
    sourceRoots.from(project(":api").sourceSets.main.map { it.java.srcDirs })
}

tasks {
    jar {
        from(configurations.runtimeClasspath.map { it.resolve().map { zipTree(it) } })
    }

    register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets.main.map { it.allJava }, project(":api").sourceSets.main.map { it.allJava })
    }

    register<Jar>("javadocJar") {
        archiveClassifier.set("alt-javadoc")
        from(javadoc.map { it.destinationDir!! })
    }

    javadoc {
        source(project(":api").sourceSets.main.map { it.allJava })
        options.encoding = "UTF-8"
        options.memberLevel = JavadocMemberLevel.PUBLIC
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    pom {
        name.set("PlatformUtils")
        description.set("A toolbox for platform independent plugins.")
        url.set("https://github.com/CrazyDev05/PlatformUtils")
        developers {
            developer {
                id.set("crazydev22")
                name.set("Julian Krings")
                roles.set(listOf("Project starter"))
            }
        }
        scm {
            connection.set("https://github.com/CrazyDev05/PlatformUtils")
            developerConnection.set("https://github.com/CrazyDev05/PlatformUtils.git")
            url.set("https://github.com/CrazyDev05/PlatformUtils")
        }
    }
}

publishing.publications.whenObjectAdded {
    if (this !is MavenPublication) return@whenObjectAdded
    artifact(tasks["javadocJar"])
}