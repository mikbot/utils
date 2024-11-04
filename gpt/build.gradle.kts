import dev.schlaubi.mikbot.gradle.mikbot
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "2.0.0"

dependencies {
    optionalPlugin(mikbot(libs.mikbot.gdpr))
}

mikbotPlugin {
    description = "Die erste menschliche KI"
    bundle = "deppgpt"
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xcontext-receivers")
        }
    }
}
