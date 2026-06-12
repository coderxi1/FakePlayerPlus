package com.coderxi.plugin.fakeplayer.manager

import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent.Move
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.scope.FakePlayerScope
import org.bukkit.Chunk.getChunkKey
import java.util.concurrent.ConcurrentHashMap
import java.util.UUID

object FakePlayerRegistry: PluginContext {

    private val scopes = ConcurrentHashMap<UUID, FakePlayerScope>()
    private val fakeplayers = ConcurrentHashMap<UUID, FakePlayer>()

    fun scopes(): Collection<FakePlayerScope> = scopes.values
    val scopesCount: Int get() = scopes.size
    fun getScope(scopeId: UUID): FakePlayerScope? = scopes[scopeId]
    fun registerScope(scope: FakePlayerScope) { scopes[scope.uniqueId] = scope }
    fun unregisterScope(uuid: UUID) = scopes.remove(uuid)

    fun fakeplayers(): Collection<FakePlayer> = fakeplayers.values
    val fakeplayersCount: Int get() = fakeplayers.size
    fun getFakePlayer(uniqueId: UUID): FakePlayer? = fakeplayers[uniqueId]

    fun registerFakePlayer(fakePlayer: FakePlayer) {
        fakeplayers[fakePlayer.uniqueId] = fakePlayer
    }
    fun unregisterFakePlayer(uuid: UUID) {
        fakeplayers.remove(uuid)
        chunkIndexedFakeplayers.values.forEach { it.remove(uuid) }
    }

    private val chunkIndexedFakeplayers = ConcurrentHashMap<Long, MutableSet<UUID>>()
    fun fakeplayersInChunk(chunkKey: Long): Collection<FakePlayer>? {
        val uuids = chunkIndexedFakeplayers[chunkKey] ?: return null
        return uuids.mapNotNull { fakeplayers[it] }
    }
    fun registerFakePlayerChunk(fakePlayer: FakePlayer) {
        val loc = fakePlayer.player.location
        val initialChunkKey = getChunkKey(loc.blockX shr 4, loc.blockZ shr 4)
        chunkIndexedFakeplayers.computeIfAbsent(initialChunkKey) {ConcurrentHashMap.newKeySet()}.add(fakePlayer.uniqueId)
        fakePlayer.on<Move> { event ->
            val fromX = event.from.blockX shr 4
            val fromZ = event.from.blockZ shr 4
            val toX = event.to.blockX shr 4
            val toZ = event.to.blockZ shr 4
            if (fromX != toX || fromZ != toZ) {
                val oldChunkKey = getChunkKey(fromX, fromZ)
                chunkIndexedFakeplayers[oldChunkKey]?.remove(fakePlayer.uniqueId)
                val newChunkKey = getChunkKey(toX, toZ)
                chunkIndexedFakeplayers.computeIfAbsent(newChunkKey) {ConcurrentHashMap.newKeySet()}.add(fakePlayer.uniqueId)
            }
        }
    }

    init {
        onPluginDisable {
            scopes.keys.toList().forEach { getScope(it)?.destroy() }
            scopes.clear()
            fakeplayers.values.forEach { it.quit("Plugin Disabled") }
            fakeplayers.clear()
        }
    }

}