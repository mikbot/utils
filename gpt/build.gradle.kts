import dev.schlaubi.mikbot.gradle.GenerateDefaultTranslationBundleTask
import dev.schlaubi.mikbot.gradle.mikbot
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "1.0.5"

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

    val generateDefaultBundle by registering(GenerateDefaultTranslationBundleTask::class) {
        defaultLocale = Locale("en", "GB")
    }

    classes {
        dependsOn(generateDefaultBundle)
    }
}
