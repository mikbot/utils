import dev.schlaubi.mikbot.gradle.mikbot

plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "5.0.0"

dependencies {
    implementation(libs.lettuce)
    implementation(libs.kotlinx.coroutines.protobuf)
    optionalPlugin(mikbot(libs.mikbot.kubernetes))
}

mikbotPlugin {
    description = "Plugin adding support to post server counts to server lists using botblock"
}
