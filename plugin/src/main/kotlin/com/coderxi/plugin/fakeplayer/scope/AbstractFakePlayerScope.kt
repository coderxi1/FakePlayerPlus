package com.coderxi.plugin.fakeplayer.scope

import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent.*
import com.coderxi.plugin.fakeplayer.factory.FakePlayerFactory
import org.bukkit.Location
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractFakePlayerScope(override val uniqueId: UUID): FakePlayerScope {

    init { registry.registerScope(this) }

    protected val fakeplayers = ConcurrentHashMap<UUID, FakePlayer>()
    override fun fakeplayers() = fakeplayers.values

    protected fun uuid(name: String): UUID = UUID.nameUUIDFromBytes("FakePlayer:$uniqueId:$name".toByteArray())

    protected fun forceSpawnAsync(uuid: UUID, name: String, spawnLocation: Location, onSpawn: (FakePlayer) -> Unit) = FakePlayerFactory.spawnAsync(uuid, name, spawnLocation) { fakePlayer ->
        fakeplayers[uuid] = fakePlayer
        onSpawn(fakePlayer)
        fakePlayer.apply {
            setPing(-1)
            player.apply {
                isPersistent = true
                isSleepingIgnored = true
                isInvulnerable = false
                isCollidable = true
                canPickupItems = true
                health = 20.0
                foodLevel = 20
            }
            on<PostQuit> {
                fakeplayers.remove(uuid)
            }
            on<AfterSpawn> {
                val locationText = "%.2f, %.2f, %.2f".format(player.location.x, player.location.y, player.location.z)
                notify(tlp("fakeplayer.spawn.success", name, player.world.name, locationText))
            }
        }
    }

    override fun remove(uuid: UUID) {
        fakeplayers.remove(uuid)
        registry.getFakePlayer(uuid)?.quit()
    }

    override fun destroy() {
        fakeplayers.values.forEach{it.quit("Scope Destroyed")}
        registry.unregisterScope(this.uniqueId)
    }

}