dependencies {
    compileOnly(project(":api"))
    compileOnly(libs.spigot)
    implementation(libs.adventure.legacy)
    implementation(libs.adventure.gson)
    implementation(libs.adventure.platform)
}

tasks.javadoc {
    enabled = false
}