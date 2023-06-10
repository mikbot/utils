plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.mikbot.gradle.plugin)
    implementation(libs.ksp.plugin)
}
