plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "3.4.0"

repositories {
    maven("https://repo.codemc.io/repository/maven-public")
}

dependencies {
    implementation(libs.bundles.botblock)
}

mikbotPlugin {
    description = "Plugin adding support to post server counts to server lists using botblock"
}
