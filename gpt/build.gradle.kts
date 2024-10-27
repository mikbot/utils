import dev.schlaubi.mikbot.gradle.GenerateDefaultTranslationBundleTask
import dev.schlaubi.mikbot.gradle.mikbot
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "1.5.0"

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
        defaultLocale = Locale.Builder().apply {
            setLanguage("en")
            setRegion("GB")
        }.build()
    }

    classes {
        dependsOn(generateDefaultBundle)
    }
}
