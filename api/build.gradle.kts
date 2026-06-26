plugins {
    id("maven-publish")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${project.findProperty("paper-api.version.base")}")
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/xiplugin/FakePlayerPlus")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}