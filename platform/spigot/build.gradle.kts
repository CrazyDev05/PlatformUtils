dependencies {
    compileOnly(project(":api"))
    compileOnly(libs.spigot)
    api(libs.adventure.legacy)
    api(libs.adventure.gson)
}

tasks.javadoc {
    enabled = false
}