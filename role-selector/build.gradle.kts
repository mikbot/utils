plugins {
    mikbot
    alias(libs.plugins.kotlin.serialization)
}

version = "4.0.0"

mikbotPlugin {
    description = "Give Roles on a specific Event"
    bundle = "roleselector"
}
