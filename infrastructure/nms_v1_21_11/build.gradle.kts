plugins {
    id("io.papermc.paperweight.userdev")
}

dependencies {
    implementation(project(":api"))
    compileOnly(project(":infrastructure:common"))
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.21.11-R0.1-SNAPSHOT")
}