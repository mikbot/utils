import dev.schlaubi.mikbot.gradle.mikbot

plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "5.1.0"

dependencies {
    plugin(mikbot(libs.mikbot.ktor))
}

mikbotPlugin {
    description = "Adds a webhook that notifies users about every free games on epicgames.com"
}
