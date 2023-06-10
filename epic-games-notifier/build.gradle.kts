plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "3.9.0"

dependencies {
    plugin(libs.mikbot.ktor)
}

mikbotPlugin {
    description = "Adds a webhook that notifies users about every free games on epicgames.com"
}
