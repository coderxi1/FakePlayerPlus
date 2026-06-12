package com.coderxi.plugin.fakeplayer.factory

import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent.*
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.manager.FakePlayerRegistry
import com.coderxi.plugin.fakeplayer.utils.IPGenerator
import org.bukkit.Location
import java.util.UUID
import java.util.concurrent.CompletableFuture

object FakePlayerFactory: PluginContext {

    private val registry = FakePlayerRegistry

    val spawnAsyncNull: CompletableFuture<FakePlayer?> = CompletableFuture.completedFuture<FakePlayer?>(null)

    fun spawnAsync(uuid: UUID, name: String, spawnLocation: Location, onSpawn:(FakePlayer)->Unit): CompletableFuture<FakePlayer?> {
        return CompletableFuture.supplyAsync {
            val nmsPlayer = nmsServer.newPlayer(uuid, name).apply {
                setPlayBefore()
                disableAdvancements(plugin)
            }
            val nmsNetwork = nms.createNetwork(IPGenerator.next(), plugin)
            Triple(nmsPlayer, nmsNetwork, spawnLocation)
        }.thenComposeAsync({ (nmsPlayer, nmsNetwork, spawnLocation) ->
            val fakePlayer = FakePlayer(nmsPlayer)
            fakePlayer.emit(PreSpawn)
            fakePlayer.connection = nmsNetwork.placeNewPlayer(fakePlayer.player)
            registry.registerFakePlayer(fakePlayer)
            onSpawn(fakePlayer)
            fakePlayer.on<PostQuit> {
                registry.unregisterFakePlayer(uuid)
            }
            fakePlayer.emit(PostSpawn)
            fakePlayer.teleportAsync(spawnLocation).thenApply { success ->
                if (success == true) {
                    registry.registerFakePlayerChunk(fakePlayer)
                    fakePlayer.emit(AfterSpawn)
                    fakePlayer
                } else {
                    fakePlayer.quit("Spawn failed")
                    registry.unregisterFakePlayer(uuid)
                    null
                }
            }
        }, scheduler.getMainThreadExecutor(plugin))
    }

}