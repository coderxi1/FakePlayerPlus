plugins {
    id("com.gradleup.shadow") version "9.2.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

repositories {
    maven("https://repo.okaeri.cloud/releases")
    // PlaceholderAPI Repo
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":infrastructure:common"))
    implementation(project(":infrastructure:nms_v1_21_11"))
    compileOnly("io.papermc.paper:paper-api:${project.findProperty("paper-api.version.base")}")
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:6.1.0-beta.1")
    // PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.12.2")
    testImplementation(kotlin("test"))
}

tasks {
    runServer {
        minecraftVersion("1.21.11")
        doFirst {
            val eulaFile = project.layout.projectDirectory.file("run/eula.txt").asFile
            if (!eulaFile.exists()) {
                eulaFile.parentFile.mkdirs()
                eulaFile.createNewFile()
            }
            eulaFile.writeText("eula=true")
        }
    }
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.jar {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("original")
    archiveVersion.set(project.version.toString())
}

tasks.shadowJar {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())
    relocate("eu.okaeri", "${project.group}.libs.okaeri")
    mergeServiceFiles()
    minimize()
    doLast {
        copy {
            from(archiveFile)
            into(rootProject.layout.buildDirectory.dir("libs"))
            rename { rootProject.name + "-" + project.version + ".jar" }
        }
    }
}