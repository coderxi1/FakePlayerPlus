package com.coderxi.plugin.fakeplayer.event

import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent.*
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.utils.ChunkExtensions.fakePlayerCount
import io.papermc.paper.event.packet.PlayerChunkLoadEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.event.player.*

class FakePlayerEventListener(private val manager: FakePlayerManager): PluginContext, Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun onFakePlayerQuit(event: PlayerQuitEvent) {
        manager.get(event.player.uniqueId)?.emit(Quit(event.quitMessage()))
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun onFakePlayerPostQuit(event: PlayerQuitEvent) {
        manager.get(event.player.uniqueId)?.let { scheduler.runTaskLater(plugin, Runnable { it.emit(PostQuit) }, 1) }
    }
    @EventHandler
    fun onFakePlayerDeath(event: PlayerDeathEvent) {
        manager.get(event.player.uniqueId)?.let { event.deathMessage(null); it.emit(Death(event.player.location)) }
    }
    @EventHandler
    fun onFakePlayerRespawn(event: PlayerRespawnEvent) {
        manager.get(event.player.uniqueId)?.emit(Respawn)
    }
    @EventHandler
    fun onFakePlayerDamage(event: EntityDamageEvent) {
        if (event.entity is Player) manager.get(event.entity.uniqueId)?.emit(Damage(event.finalDamage))
    }
    @EventHandler
    fun onFakePlayerDamage(event: EntityRegainHealthEvent) {
        if (event.entity is Player) manager.get(event.entity.uniqueId)?.emit(RegainHealth(event.amount))
    }
    @EventHandler
    fun onFakePlayerLevelChange(event: PlayerLevelChangeEvent) {
        manager.get(event.player.uniqueId)?.emit(LevelChange)
    }
    @EventHandler
    fun onFakePlayerExpChange(event: PlayerExpChangeEvent) {
        manager.get(event.player.uniqueId)?.emit(ExpChange)
    }

    @EventHandler
    fun onFakePlayerInteract(event: PlayerInteractEntityEvent) {
        manager.get(event.rightClicked.uniqueId)?.emit(Interact(event.player, event.hand))
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerChunkLoad(event: PlayerChunkLoadEvent) {
        if (event.chunk.fakePlayerCount<=0 || event.chunk.entities.isEmpty()) return
        event.chunk.entities.filterIsInstance<Player>().forEach {
            manager.get(it.uniqueId)?.emit(EnterView(event.player))
        }
    }

}