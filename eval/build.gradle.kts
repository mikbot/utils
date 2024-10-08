import dev.schlaubi.mikbot.gradle.mikbot

plugins {
    mikbot
}

version = "3.3.0"

dependencies {
    implementation(libs.rhino)
    implementation(mikbot(libs.mikbot.haste.client))
}

mikbotPlugin {
    description = "Plugin allowing users to execute code."
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
        test {
            kotlin.srcDir("build/generated/ksp/test/kotlin")
        }
    }
}
