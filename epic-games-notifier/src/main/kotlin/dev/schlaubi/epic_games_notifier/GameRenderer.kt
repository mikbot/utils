package dev.schlaubi.epic_games_notifier

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.toMessageFormat
import dev.kord.rest.builder.message.EmbedBuilder
import dev.schlaubi.mikbot.plugin.api.util.embed
import io.ktor.http.*

fun Game.toEmbed(): EmbedBuilder = embed {
    title = this@toEmbed.title
    description = this@toEmbed.description
    url = if (offerType == "BUNDLE") {
        "https://store.epicgames.com/${Config.COUNTRY_CODE.lowercase()}/bundles/$productSlug"
    } else {
        val slug = offerMappings.firstOrNull()?.pageSlug ?: productSlug ?: urlSlug
        "https://store.epicgames.com/${Config.COUNTRY_CODE.lowercase()}/p/$slug"
    }

    field {
        name = "Original Price"
        value = price.totalPrice.fmtPrice.originalPrice
    }

    val endDate =promotions!!.promotionalOffers.flatMap { it.promotionalOffers }.first().endDate
    if (endDate != null) {
        field {
            name = "Available Until"
            value = endDate.toMessageFormat(DiscordTimestampStyle.LongDate)
        }
    }


    image = (keyImages.firstOrNull { it.type == "OfferImageWide" } ?: keyImages.first()).url.encodeURLQueryComponent()
    val thumbnail = keyImages.firstOrNull { it.type == "Thumbnail" }?.url
    if (thumbnail != null) {
        thumbnail {
            url = thumbnail.encodeURLQueryComponent()
        }
    }
}
