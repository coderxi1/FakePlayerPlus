package com.coderxi.plugin.fakeplayer.event

import com.coderxi.plugin.fakeplayer.api.event.FakePlayerDeathEvent
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerInteractedEvent
import com.coderxi.plugin.fakeplayer.api.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.command.permission.Permission
import com.coderxi.plugin.fakeplayer.config.DeathEventAction
import com.coderxi.plugin.fakeplayer.provider.invsee.InvseeProvider
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot

class FakePlayerBehaviorImplementListener(private val fpm: FakePlayerManager): Listener, PluginComponent {

    val config get() = plugin.config

    @EventHandler
    fun implementInteractedInvsee(event: FakePlayerInteractedEvent) {
        if (event.hand != EquipmentSlot.HAND) return
        if (!event.player.hasPermission(Permission.INVSEE.value)) return
        if (!fpm.isOwned(event.player.uniqueId,event.fakePlayer.uuid)) return
        InvseeProvider.current.openInventory(event.player, event.fakePlayer.player)
        event.fakePlayer.world.playSound(event.fakePlayer.location, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
    }

    @EventHandler
    fun implementDeathAction(event: FakePlayerDeathEvent) {
        when (config.behavior.deathAction) {
            DeathEventAction.NONE -> { }
            DeathEventAction.QUIT -> {
                event.fakePlayer.quit()
            }
            DeathEventAction.RESPAWN -> {
                event.fakePlayer.respawn()
            }
            DeathEventAction.RESPAWN_BACK -> {
                plugin.server.scheduler.runTaskLater( plugin, Runnable {
                    event.fakePlayer.respawn()
                    event.fakePlayer.player.lastDeathLocation?.let(event.fakePlayer::teleportAsync)
                },20)
            }
        }
    }

    @EventHandler
    fun implementFollowQuiting(event: PlayerQuitEvent) {
        if (!config.behavior.followQuiting) return
        scheduler.runTaskLater(plugin, Runnable {
            if (Bukkit.getPlayer(event.player.uniqueId)!=null) return@Runnable
            fpm.fakeplayersByOwnerUuid(event.player.uniqueId).forEach { fakePlayer ->
                fakePlayer.quit("Follow Quiting")
            }
        }, (config.behavior.followQuitingDelay * 20).coerceAtLeast(0).toLong())
    }

}