plugins {
    mikbot
}

version = "2.9.0"

dependencies {
    implementation(libs.rhino)
    implementation(libs.mikbot.haste.client)
    ksp(libs.kordex.processor)
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
