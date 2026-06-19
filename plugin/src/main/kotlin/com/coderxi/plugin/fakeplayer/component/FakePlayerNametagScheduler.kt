package com.coderxi.plugin.fakeplayer.component

import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerConnectedEvent
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerDeathEvent
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerQuitEvent
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerRespawnEvent
import com.coderxi.plugin.fakeplayer.api.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class FakePlayerNametagScheduler(val fpm: FakePlayerManager): Listener, PluginComponent {

    val tagConfig get() = plugin.config.name.tag

    private val nametagMap = ConcurrentHashMap<UUID, Component>()

    val FakePlayer.nametag get() = nametagMap[uuid]

    init {
        onPluginReload {
            fpm.fakeplayers().forEach {
                it.hideVirtualNametag(Bukkit.getOnlinePlayers())
                val nametag = deserialize(tagConfig.unlimitedTemplateLines.joinToString("<br>"),it)
                it.showVirtualNametag(Bukkit.getOnlinePlayers(), nametag)
            }
        }
    }

    @EventHandler
    fun onFakePlayerConnected(event: FakePlayerConnectedEvent) {
        if (!tagConfig.unlimited) return
        val nametag = deserialize(tagConfig.unlimitedTemplateLines.joinToString("<br>"),event.fakePlayer)
        nametagMap[event.fakePlayer.uuid] = nametag
        event.fakePlayer.showVirtualNametag(Bukkit.getOnlinePlayers(),nametag)
    }

    @EventHandler
    fun onFakePlayerQuitEvent(event: FakePlayerQuitEvent) {
        if (!tagConfig.unlimited) return
        event.fakePlayer.hideVirtualNametag(Bukkit.getOnlinePlayers())
    }

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerChangedWorldEvent) {
        if (!tagConfig.unlimited) return
        scheduler.runTaskLater(plugin, Runnable {
            fpm.fakeplayers().filter { it.nametag!= null }.forEach { it.showVirtualNametag(listOf(event.player),it.nametag!! ) }
        },10)
    }

    @EventHandler
    fun onFakePlayerDeathEvent(event: FakePlayerDeathEvent) {
        if (!tagConfig.unlimited) return
        event.fakePlayer.hideVirtualNametag(Bukkit.getOnlinePlayers())
    }

    @EventHandler
    fun onFakePlayerRespawn(event: FakePlayerRespawnEvent) {
        if (!tagConfig.unlimited) return
        scheduler.runTaskLater(plugin, Runnable {
            event.fakePlayer.nametag?.let { event.fakePlayer.showVirtualNametag(Bukkit.getOnlinePlayers(),it) }
        },2)
    }

    val nametagArgsSetters: MutableMap<String, (FakePlayer) -> String> = mutableMapOf(
        "{fakeplayer_name}" to { fp -> fp.name },
        "{fakeplayer_health}" to { fp -> "%.1f".format(fp.player.health)},
        "{fakeplayer_level}" to { fp -> fp.player.level.toString() },
        "{fakeplayer_expProgress}" to { fp -> "${(fp.player.exp * 100).toInt()}%"},
        "{fakeplayer_expToLevel}" to { fp -> "${(fp.player.exp * fp.player.expToLevel).toInt()}/${fp.player.expToLevel}"},
        "{fakeplayer_status_sprites}" to { "NONE" }
    )

    private fun deserialize(s: String, fakePlayer: FakePlayer): Component {
        var ss = s
        nametagArgsSetters.forEach { (key,setter) ->
            ss = ss.replace(key,setter(fakePlayer))
        }
        return MiniMessage.miniMessage().deserialize(ss)
    }

}