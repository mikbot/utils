import dev.schlaubi.mikbot.gradle.mikbot

plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "3.4.0"

dependencies {
    optionalPlugin(mikbot(libs.mikbot.gdpr))
    optionalPlugin(mikbot(libs.mikbot.ktor))
    implementation(libs.kmongo.id.serialization)
}

mikbotPlugin {
    description= "Adds a leaderboard"
}

