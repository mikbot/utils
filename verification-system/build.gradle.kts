plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "2.11.0"

dependencies {
    plugin(libs.mikbot.ktor)
}

mikbotPlugin {
    description = "Plugin requiring each invite of the bot to be manually confirmed by an owner"
}
