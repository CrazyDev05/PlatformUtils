dependencies {
    compileOnly(project(":api"))
    compileOnly(libs.spigot)
}

tasks.javadoc {
    enabled = false
}