package com.coderxi.plugin.fakeplayer.event

import com.coderxi.plugin.fakeplayer.api.manager.FakePlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class FakePlayerDummyVarsNotifyListener(private val fpm: FakePlayerManager): Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        if (fpm.get(event.player.uniqueId) != null) return
        fpm.fakeplayers().forEach { it.nms.dummyNotify(listOf(event.player)) }
    }

}