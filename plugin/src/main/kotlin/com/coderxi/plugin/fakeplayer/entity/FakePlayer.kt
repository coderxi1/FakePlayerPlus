package com.coderxi.plugin.fakeplayer.entity

import com.coderxi.plugin.fakeplayer.api.nms.*
import com.coderxi.plugin.fakeplayer.config.OnDeathAction
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.event.FakePlayerEvent
import com.coderxi.plugin.fakeplayer.event.FakePlayerEvent.*
import com.coderxi.plugin.fakeplayer.scope.FakePlayerScope
import com.coderxi.plugin.fakeplayer.utils.EventBus
import com.coderxi.plugin.fakeplayer.utils.EventDispatcher
import net.kyori.adventure.text.Component
import org.bukkit.Location
import java.util.concurrent.CompletableFuture

class FakePlayer(
    private val handle: NMSServerPlayer,
    private val network: NMSNetwork,
    val scope: FakePlayerScope,
    override val eventBus: EventBus = EventBus()
) : EventDispatcher<FakePlayerEvent>, PluginContext {

    private val player = handle.getPlayer()
    val uniqueId = player.uniqueId

    fun spawn() {
        emit(FakePlayerEvent.PreSpawn)
        network.apply {
            placeNewPlayer(player)
            getServerGamePacketListener().setPing(-1)
        }
        player.apply {
            isPersistent = true
            isSleepingIgnored = true
            isInvulnerable = false
            isCollidable = true
            canPickupItems = true
            health = 20.0
            foodLevel = 20
        }
        emit(FakePlayerEvent.PostSpawn)
    }

    fun chat(message: String) {
        player.chat(message)
    }

    fun quit(cause: String = "quit") {
        player.kick(Component.text(cause))
    }

    fun doTick() {
        handle.doTick()
    }

    fun requestRespawn() {
        handle.requestRespawn()
    }

    fun teleportAsync(location: Location): CompletableFuture<Boolean> {
        return player.teleportAsync(location)
    }

    private var respawnBackLocation: Location? = null

    init {
        on<Death> { event ->
            when (config.onDeathAction) {
                OnDeathAction.NONE -> null
                OnDeathAction.QUIT -> Runnable { quit("You died") }
                OnDeathAction.RESPAWN -> Runnable { requestRespawn() }
                OnDeathAction.RESPAWN_BACK ->  Runnable {
                    respawnBackLocation = event.location
                    requestRespawn()
                }
            }?.also { scheduler.runTaskLater(plugin, it, 20) }
        }
        on<Respawn> {
            if (respawnBackLocation != null) {
                teleportAsync(respawnBackLocation!!).thenAccept {
                    respawnBackLocation = null
                }
            }
        }
    }


}