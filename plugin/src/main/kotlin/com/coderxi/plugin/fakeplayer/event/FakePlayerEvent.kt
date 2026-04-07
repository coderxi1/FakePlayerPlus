package com.coderxi.plugin.fakeplayer.event

import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.manager.FakePlayerRegistry
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent

sealed class FakePlayerEvent {

    object PreSpawn: FakePlayerEvent()
    object PostSpawn: FakePlayerEvent()
    object Respawn: FakePlayerEvent()
    data class Quit(val reason: Component?) : FakePlayerEvent()
    data class Death(val location: Location?) : FakePlayerEvent()
    object PostQuit : FakePlayerEvent()

    class ForwardListener: PluginContext, Listener {
        private val registry = FakePlayerRegistry
        init {
            onPluginEnable {
                plugin.server.pluginManager.registerEvents(this, plugin)
            }
            onPluginDisable {
                HandlerList.unregisterAll(this)
            }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
        fun onFakePlayerQuit(event: PlayerQuitEvent) {
            registry.getFakePlayer(event.player.uniqueId)?.emit(Quit(event.quitMessage()))
        }
        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        fun onFakePlayerPostQuit(event: PlayerQuitEvent) {
            registry.getFakePlayer(event.player.uniqueId)?.let { scheduler.runTaskLater(plugin, Runnable { it.emit(PostQuit) }, 1) }
        }
        @EventHandler
        fun onFakePlayerDeath(event: PlayerDeathEvent) {
            registry.getFakePlayer(event.player.uniqueId)?.emit(Death(event.player.location))
        }
        @EventHandler
        fun onFakePlayerRespawn(event: PlayerRespawnEvent) {
            registry.getFakePlayer(event.player.uniqueId)?.emit(Respawn)
        }

    }
}