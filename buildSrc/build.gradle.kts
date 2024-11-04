import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    gradlePluginPortal()
    maven("https://releases-repo.kordex.dev")
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.mikbot.gradle.plugin)
    implementation(libs.ksp.plugin)
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}
