package dev.schlaubi.mikbot.util_plugins.verification

import com.kotlindiscord.kord.extensions.koin.KordExKoinComponent
import dev.kord.common.entity.DiscordPartialGuild
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.unwrap
import dev.kord.core.Kord
import dev.schlaubi.mikbot.util_plugins.ktor.api.KtorExtensionPoint
import dev.schlaubi.mikbot.util_plugins.ktor.api.buildBotUrl
import dev.schlaubi.stdx.logging.debugInlined
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.bson.types.ObjectId
import org.koin.core.component.inject
import org.pf4j.Extension
import kotlin.collections.set
import dev.kord.rest.route.Route.Companion as DiscordRoute

@Serializable
@Resource("/invitations")
class Invitations {

    @Serializable
    @Resource("/{id}")
    data class Specific(val id: String, val invitations: Invitations) {
        @Serializable
        @Resource("/accept")
        data class Accept(val specific: Specific)
    }
}

private val states = mutableMapOf<String, Invitation>()
private val httpClient = HttpClient {
    install(ContentNegotiation) {
        val json = Json {
            ignoreUnknownKeys = true
        }
        json(json)
    }
}
private val LOG = KotlinLogging.logger { }
private fun notConfigured(): Nothing = error("Please set all verify env vars")

@Serializable
@Resource("/thanks")
data class Thanks(
    val state: String,
    val code: String
)

@Extension
class VerificationServer : KtorExtensionPoint, KordExKoinComponent {
    private val verifyClientId = Config.DISCORD_CLIENT_ID ?: notConfigured()
    private val verifyClientSecret = Config.DISCORD_CLIENT_SECRET ?: notConfigured()
    private val kord: Kord by inject()

    override fun Application.apply() {
        routing {
            get<Invitations.Specific.Accept> { (parent) ->
                val id = parent.id
                val invitation = VerificationDatabase.invites.findOneById(ObjectId(id))
                    ?: notFound()
                val botGuild = VerificationDatabase.collection
                    .findOneById(invitation.guildId) ?: VerificationListEntry(invitation.guildId, true)

                VerificationDatabase.invites.deleteOneById(invitation.id)
                VerificationDatabase.collection.save(botGuild.copy(verified = true))

                val state = generateNonce()
                states[state] = invitation

                val authorizeUrl =
                    kord.generateInviteForGuild(botGuild.guildId, state, invitation.type).toString()
                call.respondRedirect(authorizeUrl)
            }

            @OptIn(InternalAPI::class)
            get<Thanks> { (state, code) ->
                val invitation = states[state] ?: notFound()
                VerificationDatabase.invites.deleteOneById(invitation.id)

                val response = httpClient.post(DiscordRoute.baseUrl) {
                    expectSuccess = false // handler is underneath request

                    url {
                        path("api", "oauth2", "token")
                    }

                    val data = Parameters.build {
                        append("client_id", verifyClientId)
                        append("client_secret", verifyClientSecret)
                        append("code", code)
                        append("grant_type", "authorization_code")
                        append("redirect_uri", redirectUri)
                    }

                    body = FormDataContent(data)
                }
                if (response.status.value in 200..299) {
                    response.validateInformation(invitation)
                    call.respond("Thanks for using our bot, and please don't buy Apple products!!")
                    LOG.debugInlined { "API responded ${response.bodyAsText()}" }
                } else {
                    call.respond("An error occurred: " + response.bodyAsText())
                }
            }
        }
    }
}


private val redirectUri = buildBotUrl { path("thanks") }.toString()
private fun notFound(): Nothing = throw NotFoundException()

private fun Kord.generateInviteForGuild(guildId: Snowflake, state: String, type: InviteType): Url {
    val scope = buildList {
        if (type == InviteType.GUILD) {
            add("bot")
        }
        add("applications.commands")
        add("identify")
    }
    return URLBuilder("https://discord.com/oauth2/authorize").apply {
        parameters.apply {
            append("client_id", selfId.toString())
            append("scope", scope.joinToString(" "))
            append("redirect_uri", redirectUri)
            append("response_type", "code")
            append("state", state)
            append("integration_type", type.integrationType.value.toString())
            if (type == InviteType.GUILD) {
                append("guild_id", guildId.toString())
                append("disable_guild_select", "true")
                append("permissions", Config.DISCORD_INVITE_PERMISSION.toString())
            }
        }
    }.build()
}

private suspend fun HttpResponse.validateInformation(invitation: Invitation) {
    val (token, guild) = body<OAuthToken>()
    if (invitation.type == InviteType.GUILD) {
        if (invitation.guildId != guild.unwrap(DiscordPartialGuild::id)) {
            revokeToken(token)
        }
    } else {
        val response = httpClient.get(DiscordRoute.baseUrl) {
            url {
                path("api", "oauth2", "@me")
            }

            bearerAuth(token)
        }.body<OAuthProfile>()

        if (response.user.unwrap(DiscordUser::id) != invitation.guildId) {
            revokeToken(token)
        }
    }
}

private suspend fun revokeToken(token: String): Nothing {
    httpClient.post(DiscordRoute.baseUrl) {
        url {
            path("api", "oauth2", "token", "revoke")
        }

        val body = Parameters.build {
            set("token", token)
            set("token_type_hint", "access_token")
        }

        setBody(FormDataContent(body))

        basicAuth(Config.DISCORD_CLIENT_ID!!, Config.DISCORD_CLIENT_SECRET!!)
    }
    throw BadRequestException("Invalid authorization")
}

@Serializable
private data class OAuthToken(@SerialName("access_token") val token: String, val guild: Optional<DiscordPartialGuild> = Optional.Missing())

@Serializable
private data class OAuthProfile(val user: Optional<DiscordUser> = Optional.Missing())
