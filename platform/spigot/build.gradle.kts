dependencies {
    compileOnly(project(":api"))
    compileOnly(libs.spigot)
    api(libs.adventure.legacy)
    api(libs.adventure.gson)
    api(libs.adventure.platform)
}

tasks.javadoc {
    enabled = false
}