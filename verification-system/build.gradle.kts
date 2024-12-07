import dev.schlaubi.mikbot.gradle.mikbot

plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "4.1.0"

dependencies {
    plugin(mikbot(libs.mikbot.ktor))
}

mikbotPlugin {
    description = "Plugin requiring each invite of the bot to be manually confirmed by an owner"
}
