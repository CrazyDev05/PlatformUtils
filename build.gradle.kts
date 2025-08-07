import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.dokka.gradle.engine.parameters.KotlinPlatform

plugins {
    java
    alias(libs.plugins.publish)
    alias(libs.plugins.spotless)
    alias(libs.plugins.dokka)
}

group = "de.crazydev22"

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
    apply(plugin = "java-library")
    apply(plugin = "com.diffplug.spotless")

    java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:26.0.2")
        compileOnly(rootProject.libs.adventure.api)
    }

    spotless.java {
        removeUnusedImports()
        endWithNewline()
        trimTrailingWhitespace()
        licenseHeaderFile(rootProject.file(".github/HEADER"))
            .updateYearWithLatest(true)
    }
}

dokka.dokkaSourceSets.javaMain {
    analysisPlatform = KotlinPlatform.JVM
    sourceRoots.from(project(":api").sourceSets.main.map { it.java.srcDirs })

    sourceLink {
        localDirectory.set(file("api/src/main/java"))
        remoteUrl("https://github.com/CrazyDev05/PlatformUtils/blob/master/api/src/main/java")
        remoteLineSuffix.set("#L")
    }

    sourceLink {
        localDirectory.set(file("src/main/java"))
        remoteUrl("https://github.com/CrazyDev05/PlatformUtils/blob/master/src/main/java")
        remoteLineSuffix.set("#L")
    }
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
    //signAllPublications()

    configure(JavaLibrary(JavadocJar.Javadoc()))
    pom {
        name.set("PlatformUtils")
        description.set("A toolbox for platform independent plugins.")
        url.set("https://github.com/CrazyDev05/PlatformUtils")
        licenses {
            license {
                name.set("The MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
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