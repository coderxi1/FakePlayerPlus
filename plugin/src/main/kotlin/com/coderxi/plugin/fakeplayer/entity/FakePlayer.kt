package com.coderxi.plugin.fakeplayer.entity

import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent.*
import com.coderxi.plugin.fakeplayer.api.nms.*
import com.coderxi.plugin.fakeplayer.config.OnDeathAction
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.provider.invsee.InvseeProvider
import com.coderxi.plugin.fakeplayer.utils.EventEmitter
import com.coderxi.plugin.fakeplayer.utils.EventDispatcher
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class FakePlayer(private val nmsPlayer: NMSServerPlayer) : EventDispatcher<FakePlayerEvent>, PluginContext {

    companion object : PluginContext {
        val invseeProvider: InvseeProvider by lazy { plugin.config.invseeProviderType.newInstance() }
    }

    val player = nmsPlayer.getPlayer()
    val uniqueId = player.uniqueId
    lateinit var connection: NMSServerGamePacketListener
    override val emitter: EventEmitter = EventEmitter()

    val name get() = player.name

    fun setPing(ping: Int) {
        connection.setPing(ping)
    }

    fun chat(message: String) {
        player.chat(message)
    }

    fun quit(cause: String = "quit") {
        player.kick(Component.text(cause))
    }

    fun doTick() {
        nmsPlayer.doTick()
    }

    fun requestRespawn() {
        nmsPlayer.requestRespawn()
    }

    fun teleportAsync(location: Location): CompletableFuture<Boolean?> {
        return player.teleportAsync(location)
    }

    fun showVirtualNametag(player: Player, content: Component) {
        nmsPlayer.showVirtualNametag(player, content)
    }

    fun updateVirtualNametag(player: Player, content: Component) {
        nmsPlayer.updateVirtualNametag(player, content)
    }

    fun hideVirtualNametag(player: Player) {
        nmsPlayer.hideVirtualNametag(player)
    }

    fun showInventory(viewer: Player) {
        invseeProvider.openInventory(viewer,this.player)
        player.location.world.playSound(player.location, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
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
            respawnBackLocation?.let { loc ->
                respawnBackLocation = null
                player.teleportAsync(loc)
            }
        }
    }


}