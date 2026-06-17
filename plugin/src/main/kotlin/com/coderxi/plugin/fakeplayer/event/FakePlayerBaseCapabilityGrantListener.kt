package com.coderxi.plugin.fakeplayer.event

import com.coderxi.plugin.fakeplayer.api.event.FakePlayerDeathEvent
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerInteractedEvent
import com.coderxi.plugin.fakeplayer.api.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.provider.invsee.InvseeProvider
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener


class FakePlayerBaseCapabilityGrantListener(private val fpm: FakePlayerManager): Listener, PluginComponent {

    val config get() = plugin.config

    @EventHandler
    fun onFakePlayerInteractedEvent(event: FakePlayerInteractedEvent) {
        if (!fpm.isOwned(event.player,event.fakePlayer)) return
        InvseeProvider.current.openInventory(event.player, event.fakePlayer.player)
        event.fakePlayer.world.playSound(event.fakePlayer.location, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
    }

    @EventHandler
    fun onFakePlayerDeathEvent(event: FakePlayerDeathEvent) {
        plugin.server.scheduler.runTaskLater( plugin, Runnable {
            event.fakePlayer.requestRespawn()
            event.fakePlayer.player.lastDeathLocation?.let(event.fakePlayer::teleportAsync)

        },20)
    }

}