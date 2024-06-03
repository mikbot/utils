import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    org.jetbrains.kotlin.jvm
    com.google.devtools.ksp
    dev.schlaubi.mikbot.`gradle-plugin`
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_22
    }
}
