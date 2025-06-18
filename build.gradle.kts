plugins {
    java
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

tasks.javadoc {
    source(sourceSets.main.map { it.allJava }, project(":api").sourceSets.main.map { it.allJava })
}