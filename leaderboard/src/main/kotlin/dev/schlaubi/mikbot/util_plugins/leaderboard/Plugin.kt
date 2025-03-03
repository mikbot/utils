package dev.schlaubi.mikbot.util_plugins.leaderboard

import dev.kordex.core.builders.ExtensionsBuilder
import dev.schlaubi.mikbot.plugin.api.Plugin
import dev.schlaubi.mikbot.plugin.api.PluginContext
import dev.schlaubi.mikbot.plugin.api.PluginMain
import dev.schlaubi.mikbot.plugin.api.settings.SettingsExtensionPoint
import dev.schlaubi.mikbot.plugin.api.settings.SettingsModule
import dev.schlaubi.mikbot.util_plugins.leaderboard.commands.importMee6
import dev.schlaubi.mikbot.util_plugins.leaderboard.commands.leaderBoardCommand
import dev.schlaubi.mikbot.util_plugins.leaderboard.core.LeaderBoardModule
import org.pf4j.Extension

@PluginMain
class LeaderBoardPlugin(wrapper: PluginContext) : Plugin(wrapper) {
    override fun ExtensionsBuilder.addExtensions() {
        add(::LeaderBoardModule)
    }
}

@Extension
class LeaderBoardSettingsExtension : SettingsExtensionPoint {
    override suspend fun SettingsModule.apply() {
        leaderBoardCommand()
        importMee6()
    }
}
