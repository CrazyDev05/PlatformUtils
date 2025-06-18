dependencies {
    compileOnly(project(":api"))
    compileOnly(libs.folia)
}

tasks.javadoc {
    enabled = false
}