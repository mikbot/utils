plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "utils"

include(
    ":answering_machine",
    ":birthdays",
    ":botblock",
    ":brieftaube",
    ":epic-games-notifier",
    ":gpt",
    ":leaderboard",
    ":verification-system"
)
