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
