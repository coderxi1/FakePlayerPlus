package com.coderxi.plugin.fakeplayer.entity

import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent.*
import com.coderxi.plugin.fakeplayer.api.nms.*
import com.coderxi.plugin.fakeplayer.config.OnDeathAction
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.context.PluginContext.Companion.plugin
import com.coderxi.plugin.fakeplayer.provider.invsee.InvseeProvider
import com.coderxi.plugin.fakeplayer.utils.EventEmitter
import com.coderxi.plugin.fakeplayer.utils.IPGenerator
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

class FakePlayer(
    val name: String,
    val uuid: UUID = UUID.nameUUIDFromBytes("${plugin.name}:$name".toByteArray()),
    var skin: String? = null,
    var ownerUuids: Collection<UUID> = emptyList(),
) : PluginContext {

    val emitter = EventEmitter<FakePlayerEvent>()

    lateinit var nmsPlayer: NMSServerPlayer
    lateinit var nmsNetwork: NMSNetwork
    lateinit var nmsConnection: NMSServerGamePacketListener
    val player get() = nmsPlayer.player
    val world get() = player.world
    val location: Location get() = player.location

    fun connect() {
        nmsPlayer = nmsServer.newPlayer(uuid, name)
        nmsPlayer.disableAdvancements()
        nmsNetwork = nms.createNetwork(IPGenerator.next(), plugin)
        nmsConnection = nmsNetwork.placeNewPlayer(nmsPlayer.player)
        nmsConnection.setPing(-1)
    }

    fun postSpawn() {
        player.apply {
            isPersistent = true
            isSleepingIgnored = true
            isInvulnerable = false
            isCollidable = true
            canPickupItems = true
            health = 20.0
            foodLevel = 20
        }
        on<Interact>{ event ->
            InvseeProvider.current.openInventory(event.player,player)
        }
        on<Death> {
            when (config.onDeathAction) {
                OnDeathAction.NONE -> null
                OnDeathAction.QUIT -> Runnable { quit("You died") }
                OnDeathAction.RESPAWN -> Runnable { requestRespawn() }
                OnDeathAction.RESPAWN_BACK ->  Runnable { requestRespawn() }
            }?.also { scheduler.runTaskLater(plugin, it, 20) }
        }
        on<Respawn> {
            if (config.onDeathAction == OnDeathAction.RESPAWN_BACK) {
                player.lastDeathLocation?.let { player.teleportAsync(it)  }
            }
        }
    }

    fun chat(message: String) = player.chat(message)
    fun quit(cause: String = "quit") = player.kick(Component.text(cause))
    fun doTick() = nmsPlayer.doTick()
    fun requestRespawn() = nmsPlayer.requestRespawn()
    fun teleportAsync(location: Location) = player.teleportAsync(location)
    fun showVirtualNametag(player: Player, content: Component) = nmsPlayer.showVirtualNametag(player, content)
    fun updateVirtualNametag(player: Player, content: Component) = nmsPlayer.updateVirtualNametag(player, content)
    fun hideVirtualNametag(player: Player) = nmsPlayer.hideVirtualNametag(player)
    inline fun <reified T : FakePlayerEvent> on(priority: Int = 0, noinline action: (T) -> Unit) = emitter.registerEvent(priority, action)
    inline fun <reified T : FakePlayerEvent> emit(event: T) = emitter.emit(event)

}