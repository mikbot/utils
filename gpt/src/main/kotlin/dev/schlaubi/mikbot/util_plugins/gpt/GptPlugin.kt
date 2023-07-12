package dev.schlaubi.mikbot.util_plugins.gpt

import com.kotlindiscord.kord.extensions.builders.ExtensibleBotBuilder
import com.kotlindiscord.kord.extensions.extensions.Extension
import dev.schlaubi.mikbot.plugin.api.Plugin
import dev.schlaubi.mikbot.plugin.api.PluginContext
import dev.schlaubi.mikbot.plugin.api.PluginMain
import dev.schlaubi.mikbot.util_plugins.gpt.commands.newChannelCommand
import dev.schlaubi.mikbot.util_plugins.gpt.commands.newThreadCommand
import dev.schlaubi.mikbot.util_plugins.gpt.commands.startThreadContextAction

@PluginMain
class GptPlugin(context: PluginContext) : Plugin(context) {
    override fun ExtensibleBotBuilder.ExtensionsBuilder.addExtensions() {
        add(::GptExtension)
    }
}

private class GptExtension : Extension() {
    override val name: String = "gpt"
    override val bundle: String = "deppgpt"

    override suspend fun setup() {
        newChannelCommand()
        newThreadCommand()
        startThreadContextAction()
        gptExecutor()
    }
}
