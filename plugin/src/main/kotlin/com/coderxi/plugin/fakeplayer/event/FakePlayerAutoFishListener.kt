package com.coderxi.plugin.fakeplayer.event

import com.coderxi.plugin.fakeplayer.api.action.UseItemOnce
import com.coderxi.plugin.fakeplayer.api.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

class FakePlayerAutoFishListener(private val fpm: FakePlayerManager) : Listener, PluginComponent {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun onPlayerFish(event: PlayerFishEvent) {
        if (event.state != PlayerFishEvent.State.BITE) return
        val fakePlayer = fpm.get(event.player.uniqueId)?.takeIf { it.settings.autoFish } ?: return
        scheduler.runTaskLater(plugin, Runnable {
            if (!fakePlayer.player.isOnline) return@Runnable
            fakePlayer.actions.dispatch(UseItemOnce)
            scheduler.runTaskLater(plugin, Runnable {
                if (!fakePlayer.player.isOnline) return@Runnable
                fakePlayer.actions.dispatch(UseItemOnce)
            }, 20L)
        }, 1L)
    }

}