package dev.schlaubi.mikbot.eval

import com.kotlindiscord.kord.extensions.builders.ExtensibleBotBuilder
import com.kotlindiscord.kord.extensions.utils.loadModule
import dev.schlaubi.mikbot.haste.HasteClient
import dev.schlaubi.mikbot.plugin.api.Plugin
import dev.schlaubi.mikbot.plugin.api.PluginContext
import dev.schlaubi.mikbot.plugin.api.PluginMain
import org.koin.dsl.bind

@PluginMain
class EvalPlugin(wrapper: PluginContext) : Plugin(wrapper) {
    override fun ExtensibleBotBuilder.ExtensionsBuilder.addExtensions() {
        add(::EvalExtension)
    }

    override suspend fun ExtensibleBotBuilder.apply() {
        hooks {
            beforeKoinSetup {
                loadModule {
                    single { HasteClient(EvalConfig.HASTE_SERVER) } bind HasteClient::class
                }
            }
        }
    }
}
