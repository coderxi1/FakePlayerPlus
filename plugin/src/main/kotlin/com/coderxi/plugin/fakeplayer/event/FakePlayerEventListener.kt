package com.coderxi.plugin.fakeplayer.event

import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent.*
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.manager.FakePlayerRegistry
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.event.player.*

class FakePlayerEventListener: PluginContext, Listener {

    private val registry = FakePlayerRegistry
    init {
        onPluginEnable {
            plugin.server.pluginManager.registerEvents(this, plugin)
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
        registry.getFakePlayer(event.player.uniqueId)?.let { event.deathMessage(null); it.emit(Death(event.player.location)) }
    }
    @EventHandler
    fun onFakePlayerRespawn(event: PlayerRespawnEvent) {
        registry.getFakePlayer(event.player.uniqueId)?.emit(Respawn)
    }
    @EventHandler
    fun onFakePlayerDamage(event: EntityDamageEvent) {
        if (event.entity is Player) registry.getFakePlayer(event.entity.uniqueId)?.emit(Damage(event.finalDamage))
    }
    @EventHandler
    fun onFakePlayerDamage(event: EntityRegainHealthEvent) {
        if (event.entity is Player) registry.getFakePlayer(event.entity.uniqueId)?.emit(RegainHealth(event.amount))
    }
    @EventHandler
    fun onFakePlayerLevelChange(event: PlayerLevelChangeEvent) {
        registry.getFakePlayer(event.player.uniqueId)?.emit(LevelChange)
    }
    @EventHandler
    fun onFakePlayerExpChange(event: PlayerExpChangeEvent) {
        registry.getFakePlayer(event.player.uniqueId)?.emit(ExpChange)
    }
}