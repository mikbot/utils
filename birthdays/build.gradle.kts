import dev.schlaubi.mikbot.gradle.mikbot

plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "2.3.0"

dependencies {
    plugin(mikbot(libs.mikbot.ktor))
    optionalPlugin(mikbot(libs.mikbot.gdpr))
}
