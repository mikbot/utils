plugins {
    dev.schlaubi.mikbot.`gradle-plugin`
}

allprojects {
    group = "dev.schlaubi.utils"

    repositories {
        mavenCentral()
    }
}

mikbotPlugin {
    provider = "Mikbot Team"
    license = "MIT"

    i18n {
        classPackage = "dev.schlaubi.mikbot.utils.translations"
    }
}

pluginPublishing {
    targetDirectory = layout.projectDirectory.dir("ci-repo")
    projectUrl = "https://github.com/mikbot/utils"
    repositoryUrl = "https://storage.googleapis.com/mikbot-plugins"
}

subprojects {
    afterEvaluate {
        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_22
        }
    }
}
