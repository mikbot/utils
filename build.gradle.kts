import kotlin.io.path.div

plugins {
    dev.schlaubi.mikbot.`gradle-plugin`
}

allprojects {
    group = "dev.schlaubi.tonbrett"

    repositories {
        mavenCentral()
    }
}

mikbotPlugin {
    provider = "Mikbot Team"
    license = "MIT"
}

pluginPublishing {
    targetDirectory = layout.projectDirectory.dir("ci-repo")
    projectUrl = "https://github.com/mikbot/utils"
    repositoryUrl = "https://storage.googleapis.com/mikbot-plugins"
}
