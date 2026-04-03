plugins {
    id("io.papermc.paperweight.userdev")
}
dependencies {
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:${project.findProperty("paper-api.version.base")}")
}