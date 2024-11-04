package dev.schlaubi.mikbot.util_plugins.verification

import dev.kordex.core.builders.ExtensionsBuilder
import dev.kordex.core.extensions.Extension
import dev.kordex.core.koin.KordExKoinComponent
import dev.schlaubi.mikbot.plugin.api.Plugin
import dev.schlaubi.mikbot.plugin.api.PluginContext
import dev.schlaubi.mikbot.plugin.api.PluginMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

class VerificationModule : Extension() {
    override val name: String = "verification"

    override suspend fun setup() {
        verificationListeners()
    }
}

@PluginMain
class VerificationPlugin(wrapper: PluginContext) : Plugin(wrapper), CoroutineScope, KordExKoinComponent {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()

    override fun ExtensionsBuilder.addExtensions() {
        add(::VerificationModule)
    }

    override fun stop() {
        coroutineContext.cancel()
    }
}
