package com.coderxi.plugin.fakeplayer.manager.impl

import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent.AfterSpawn
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent.PostQuit
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent.PostSpawn
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent.PreSpawn
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.repository.FakePlayerRepository
import com.coderxi.plugin.fakeplayer.utils.ChunkExtensions.fakePlayerCount
import org.bukkit.Location
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

class FakePlayerManagerImpl(private val repository: FakePlayerRepository) : FakePlayerManager, PluginContext {

    private val fakeplayers = ConcurrentHashMap<UUID, FakePlayer>()

    override fun getOnlineFakePlayers() = fakeplayers.values
    override fun getOnlineFakePlayersCount(): Int = fakeplayers.size

    override fun register(fakePlayer: FakePlayer) { fakeplayers[fakePlayer.uuid] = fakePlayer }
    override fun unregister(uuid: UUID) { fakeplayers.remove(uuid) }
    override fun isFakePlayer(uuid: UUID): Boolean = fakeplayers.containsKey(uuid)

    override fun spawnAsync(name: String, location: Location): CompletableFuture<FakePlayer?> {

        val fakePlayer = repository.findByName(name) ?: FakePlayer(name).apply {
            repository.save(this)
        }
        fakePlayer.apply {
            emit(PreSpawn)
            connect()
            register(this)
            on<PostQuit>{
                player.chunk.fakePlayerCount--
                unregister(uuid)
            }
            postSpawn()
            emit(PostSpawn)
        }
        return fakePlayer.teleportAsync(location).thenApply { success ->
            if (success == true) {
                fakePlayer.apply {
                    player.chunk.fakePlayerCount++
                    emit(AfterSpawn)
                }
            } else {
                fakePlayer.quit("Spawn failed")
                null
            }
        }
    }

    override fun remove(name: String, cause: String) { get(name)?.quit(cause) }

    override fun get(uuid: UUID): FakePlayer? = fakeplayers[uuid]

    override fun get(name: String): FakePlayer? = fakeplayers.values.find { it.name.equals(name, true) }

    override fun checkSpawnLimit(ownerId: UUID): Boolean {
        //TODO
        return true
    }
}