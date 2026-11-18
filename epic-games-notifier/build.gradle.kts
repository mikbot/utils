import dev.schlaubi.mikbot.gradle.mikbot

plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
    `jvm-test-suite`
}

version = "6.2.1"

dependencies {
    plugin(mikbot(libs.mikbot.ktor))
}

testing {
    val rootProject = project
    suites {
        @Suppress("UnstableApiUsage")
        named<JvmTestSuite>("test") {
            useJUnitJupiter()
            dependencies {
                implementation(libs.ktor.client.java)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.resources)
                implementation(rootProject.dependencies.kotlin("test-junit5").toString())
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

mikbotPlugin {
    description = "Adds a webhook that notifies users about every free games on epicgames.com"
}
