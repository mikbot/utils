plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "3.0.0"

dependencies {
    optionalPlugin(libs.mikbot.gdpr)
    optionalPlugin(libs.mikbot.ktor)
    implementation(libs.kmongo.id.serialization)
}

mikbotPlugin {
    description= "Adds a leaderboard"
}

