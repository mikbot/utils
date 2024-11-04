package dev.schlaubi.mikbot.util_plugins.brieftaube

import dev.kordex.core.builders.ExtensionsBuilder
import dev.schlaubi.mikbot.plugin.api.Plugin
import dev.schlaubi.mikbot.plugin.api.PluginContext
import dev.schlaubi.mikbot.plugin.api.PluginMain
import dev.schlaubi.mikbot.plugin.api.module.MikBotModule

@PluginMain
class BrieftaubePlugin(context: PluginContext) : Plugin(context) {
    override fun ExtensionsBuilder.addExtensions() {
        add(::BrieftaubeExtension)
    }
}

private class BrieftaubeExtension(context: PluginContext) : MikBotModule(context) {
    override val name: String = "brieftaube"

    override suspend fun setup() {
        mirrorChannelCommand()
        unMirrorChannelCommand()
        mirrorChannelExecutor()
    }
}
