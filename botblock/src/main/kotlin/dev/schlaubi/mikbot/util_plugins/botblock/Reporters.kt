package dev.schlaubi.mikbot.util_plugins.botblock

import dev.kord.core.Kord
import io.github.oshai.kotlinlogging.KotlinLogging
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.Range
import io.lettuce.core.RedisClient
import io.lettuce.core.api.coroutines
import io.lettuce.core.codec.ByteArrayCodec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.collections.component1
import kotlin.collections.component2
import dev.schlaubi.mikbot.core.health.Config as KubernetesConfig

private val LOG = KotlinLogging.logger { }

interface Reporter {
    suspend fun report()
}

class SingleNodeReporter(private val kord: Kord, private val tokens: Map<String, String>) : Reporter {
    override suspend fun report() {
        val totalGuildCount = kord.rest.application.getCurrentApplicationInfo().approximateGuildCount.orElse(0)
        val shardGuilds = kord.guilds.toList()

        val byShard = shardGuilds
            .groupBy { it.gateway }
            .map { (_, value) -> value.size.toLong() }

        postStats(
            UpdateServerCountRequest(
                totalGuildCount.toLong(),
                kord.selfId.toString(),
                byShard,
                tokens
            )
        )
    }
}

private val redisKey = "guild_count_reports".toByteArray()
private val redisBodyKey = "body".toByteArray()

@OptIn(ExperimentalLettuceCoroutinesApi::class, ExperimentalSerializationApi::class)
class MultiNodeReporter(scope: CoroutineScope, private val kord: Kord, private val tokens: Map<String, String>) : Reporter {
    @Serializable
    data class ShardGuildCountReport(val countByShard: List<Long>)

    private val connection = RedisClient.create(Config.REDIS_URL)
        .connect(ByteArrayCodec.INSTANCE)
        .coroutines()

    init {
        if (KubernetesConfig.ENABLE_SCALING && KubernetesConfig.POD_ID == 0) {
            val reportCount = KubernetesConfig.TOTAL_SHARDS / KubernetesConfig.SHARDS_PER_POD
            val reports = ArrayList<ShardGuildCountReport>(reportCount)
            connection.xrange(redisKey, Range.unbounded())
                .onEach {
                    val report = ProtoBuf.decodeFromByteArray<ShardGuildCountReport>(it.body.values.first())
                    LOG.debug { "Received report $report" }
                    reports.add(report)
                    connection.xdel(redisKey, it.id)
                    if (reports.size == reportCount) {
                        LOG.debug { "Received all reports" }
                        val allGuilds = reports.flatMap { it.countByShard }
                        val approximateCount = kord.rest.application.getCurrentApplicationInfo().approximateGuildCount.orElse(0)
                        reports.clear()

                        postStats(
                            UpdateServerCountRequest(
                                approximateCount.toLong(),
                                kord.selfId.toString(),
                                allGuilds,
                                tokens
                            ))
                    }
                }
                .launchIn(scope)

        }
    }

    override suspend fun report() {
        val shardGuilds = kord.guilds.toList()
        val byShard = shardGuilds
            .groupBy { it.gateway }
            .map { (_, value) -> value.size.toLong() }
        val report = ShardGuildCountReport(byShard)

        LOG.debug { "Sending report $report" }
        connection.xadd(redisKey, mapOf(redisBodyKey to ProtoBuf.encodeToByteArray(report)))
    }
}
