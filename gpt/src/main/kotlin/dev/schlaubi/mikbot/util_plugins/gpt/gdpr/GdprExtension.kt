package dev.schlaubi.mikbot.util_plugins.gpt.gdpr

import dev.schlaubi.mikbot.core.gdpr.api.DataPoint
import dev.schlaubi.mikbot.core.gdpr.api.GDPRExtensionPoint
import dev.schlaubi.mikbot.core.gdpr.api.ProcessedData
import dev.schlaubi.mikbot.utils.translations.DeppgptTranslations
import org.pf4j.Extension

private val PostellionDataPoint = ProcessedData(
    DeppgptTranslations.Gdpr.description,
    DeppgptTranslations.Gdpr.Sharing.description,
)

@Extension
class GdprExtension : GDPRExtensionPoint {
    override fun provideDataPoints(): List<DataPoint> = listOf(PostellionDataPoint)
}
