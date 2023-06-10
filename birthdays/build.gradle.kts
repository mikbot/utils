plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "1.9.0"

dependencies {
    plugin(libs.mikbot.ktor)
    optionalPlugin(libs.mikbot.gdpr)
}
