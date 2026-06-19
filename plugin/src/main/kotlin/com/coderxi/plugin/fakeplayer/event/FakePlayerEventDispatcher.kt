package com.coderxi.plugin.fakeplayer.event

import com.coderxi.plugin.fakeplayer.api.event.*
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import com.coderxi.plugin.fakeplayer.api.manager.FakePlayerManager
import io.papermc.paper.event.packet.PlayerChunkLoadEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.event.player.*

class FakePlayerEventDispatcher(private val fpm: FakePlayerManager): Listener, PluginComponent {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun onFakePlayerQuit(event: PlayerQuitEvent) {
        fpm.get(event.player.uniqueId)?.let { FakePlayerQuitEvent(it,event.quitMessage()).callEvent() }
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun onFakePlayerPostQuit(event: PlayerQuitEvent) {
        fpm.get(event.player.uniqueId)?.let { scheduler.runTaskLater(plugin, FakePlayerQuitedEvent(it)::callEvent, 1) }
    }
    @EventHandler
    fun onFakePlayerDeath(event: PlayerDeathEvent) {
        fpm.get(event.player.uniqueId)?.let { event.deathMessage(null); FakePlayerDeathEvent(it,event.player.location).callEvent() }
    }
    @EventHandler
    fun onFakePlayerRespawn(event: PlayerRespawnEvent) {
        fpm.get(event.player.uniqueId)?.let { FakePlayerRespawnEvent(it).callEvent() }
    }
    @EventHandler
    fun onFakePlayerDamage(event: EntityDamageEvent) {
        if (event.entity is Player) fpm.get(event.entity.uniqueId)?.let { FakePlayerDamageEvent(it,event.finalDamage).callEvent() }
    }
    @EventHandler
    fun onFakePlayerDamage(event: EntityRegainHealthEvent) {
        if (event.entity is Player) fpm.get(event.entity.uniqueId)?.let { FakePlayerRegainHealthEvent(it,event.amount).callEvent() }
    }
    @EventHandler
    fun onFakePlayerLevelChange(event: PlayerLevelChangeEvent) {
        fpm.get(event.player.uniqueId)?.let { FakePlayerLevelChangeEvent(it).callEvent() }
    }
    @EventHandler
    fun onFakePlayerExpChange(event: PlayerExpChangeEvent) {
        fpm.get(event.player.uniqueId)?.let { FakePlayerExpChangeEvent(it).callEvent() }
    }

    @EventHandler
    fun onFakePlayerInteract(event: PlayerInteractEntityEvent) {
        fpm.get(event.rightClicked.uniqueId)?.let { FakePlayerInteractedEvent(it,event.player,event.hand).callEvent() }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerChunkLoad(event: PlayerChunkLoadEvent) {
        event.chunk.entities.filterIsInstance<Player>().forEach { player ->
            fpm.get(player.uniqueId)?.let { FakePlayerWatchedEvent(it,event.player).callEvent() }
        }
    }

}