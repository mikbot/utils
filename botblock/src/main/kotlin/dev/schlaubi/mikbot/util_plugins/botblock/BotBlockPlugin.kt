package dev.schlaubi.mikbot.util_plugins.botblock

import com.kotlindiscord.kord.extensions.builders.ExtensibleBotBuilder
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import dev.kord.rest.request.KtorRequestException
import dev.schlaubi.mikbot.plugin.api.Plugin
import dev.schlaubi.mikbot.plugin.api.PluginContext
import dev.schlaubi.mikbot.plugin.api.PluginMain
import dev.schlaubi.mikbot.plugin.api.util.AllShardsReadyEvent
import kotlinx.coroutines.*
import mu.KotlinLogging
import kotlin.time.Duration.Companion.minutes

private val LOG = KotlinLogging.logger { }

@PluginMain
class BotBlockPlugin(wrapper: PluginContext) : Plugin(wrapper) {
    override fun ExtensibleBotBuilder.ExtensionsBuilder.addExtensions() {
        add(::BotBlockExtension)
    }
}

class BotBlockExtension : Extension() {
    override val name: String = "botblock"
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val botTokens by lazy {
        Config.SUPPORTED_BOT_LISTS
            .associate { (name, envName) ->
                name to (System.getenv(envName) ?: error("Missing token for $name ($envName)"))
            }
    }

    override suspend fun setup() {
        event<AllShardsReadyEvent> {
            action {
                startLoop()
            }
        }
    }

    private fun startLoop() {
        val reporter = if (System.getenv("ENABLE_SCALING") == "true") {
            MultiNodeReporter(scope, kord, botTokens)
        } else {
            SingleNodeReporter(kord, botTokens)
        }

        scope.launch {
            try {
                reporter.report()
            } catch (e: KtorRequestException) {
                LOG.error(e) { "Could not post stats" }
            }
            delay(Config.BOTBLOCK_DELAY.minutes)
        }
    }

    override suspend fun unload() {
        scope.cancel()
    }
}
