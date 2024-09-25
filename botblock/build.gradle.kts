plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "3.4.1"

dependencies {
    implementation(libs.bundles.botblock)
}

mikbotPlugin {
    description = "Plugin adding support to post server counts to server lists using botblock"
}
