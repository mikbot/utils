plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "2.0.0"

dependencies {
    plugin(libs.mikbot.ktor)
    optionalPlugin(libs.mikbot.gdpr)
}
