plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "2.10.0"

mikbotPlugin {
    description = "Plugin adding support to post server counts to server lists using botblock"
}
