import dev.schlaubi.mikbot.gradle.mikbot

plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "3.3.3"

dependencies {
    plugin(mikbot(libs.mikbot.ktor))
    optionalPlugin(mikbot(libs.mikbot.gdpr))
}
