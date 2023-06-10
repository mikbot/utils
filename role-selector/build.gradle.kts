plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "3.7.0"

mikbotPlugin {
    description = "Give Roles on a specific Event"
    bundle = "roleselector"
}
