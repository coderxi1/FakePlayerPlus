package com.coderxi.plugin.fakeplayer.manager

import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent.*
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.utils.MinecraftSpritesUtil
import io.papermc.paper.event.packet.PlayerChunkLoadEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Chunk.getChunkKey
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.scheduler.BukkitTask
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.forEach

object FakePlayerNametagManager: PluginContext {

    val registry = FakePlayerRegistry
    lateinit var nametagTemplate: Component
    val nametagTargetsGetters = ConcurrentHashMap<UUID, () -> Collection<UUID>>()
    val nametagArgsSetters: MutableMap<String, (FakePlayer) -> Component> = mutableMapOf(
        "{fakeplayer_name}" to { fp -> Component.text(fp.name) },
        "{fakeplayer_health_sprites}" to { fp -> MinecraftSpritesUtil.health2heartsSprites(fp.player) },
        "{fakeplayer_health}" to { fp -> Component.text("%.1f".format(fp.player.health))},
        "{fakeplayer_level}" to { fp -> Component.text(fp.player.level) },
        "{fakeplayer_expProgress}" to { fp -> Component.text("${(fp.player.exp * 100).toInt()}%")},
        "{fakeplayer_expToLevel}" to { fp -> Component.text("${(fp.player.exp * fp.player.expToLevel).toInt()}/${fp.player.expToLevel}")},
        "{fakeplayer_status_sprites}" to { Component.empty() } //TODO
    )
    lateinit var refreshEvents: List<Class<out FakePlayerEvent>>
    var refreshTask: BukkitTask? = null
    private val updatingFakePlayers = ConcurrentHashMap.newKeySet<UUID>()

    init {
        registerEvents(Listener())
        onPluginEnable { init() }
        onPluginReload { refreshTask?.cancel(); init() }
        onPluginDisable { refreshTask?.cancel() }
    }

    fun init() {
        nametagTemplate = MiniMessage.miniMessage().deserialize(config.nametag.lines.joinToString("<br><reset>"))
        refreshEvents = config.nametag.refreshEvents.mapNotNull { name ->
            runCatching { Class.forName("com.coderxi.plugin.fakeplayer.events.FakePlayerEvent.$$name") }.getOrNull() as Class<out FakePlayerEvent>?
        }
        refreshTask = scheduler.runTaskTimerAsynchronously(plugin, Runnable {
            if (updatingFakePlayers.isEmpty()) return@Runnable
            val iterator = updatingFakePlayers.iterator()
            while (iterator.hasNext()) {
                registry.getFakePlayer(iterator.next())?.let { fakePlayer ->
                    val nametag = fillTemplate(fakePlayer)
                    nametagTargetsGetters[fakePlayer.uniqueId]?.invoke()?.forEach { uuid ->
                        Bukkit.getPlayer(uuid)?.let { target ->
                            if (target.isOnline) fakePlayer.updateVirtualNametag(target, nametag)
                        }
                    }
                }
                iterator.remove()
            }
        }, 0L, config.nametag.refreshIntervalTick)
    }

    private fun fillTemplate(fakePlayer: FakePlayer) = nametagArgsSetters.entries.fold(nametagTemplate) { nametag, (key, setter) ->
        nametag.replaceText { it.matchLiteral(key).replacement(setter(fakePlayer)) }
    }

    fun showNametag(fakePlayer: FakePlayer, nametag: Component = fillTemplate(fakePlayer)) {
        nametagTargetsGetters[fakePlayer.uniqueId]?.invoke()?.mapNotNull(Bukkit::getPlayer)?.forEach { fakePlayer.showVirtualNametag(it, nametag) }
    }

    fun updateNametag(fakePlayer: FakePlayer) {
        updatingFakePlayers.add(fakePlayer.uniqueId)
    }

    fun hideNametag(fakePlayer: FakePlayer) {
        nametagTargetsGetters[fakePlayer.uniqueId]?.invoke()?.mapNotNull(Bukkit::getPlayer)?.forEach { fakePlayer.hideVirtualNametag(it) }
    }

    fun bind(fakePlayer: FakePlayer, targetsGetter: () -> Collection<UUID>) {
        nametagTargetsGetters[fakePlayer.uniqueId] = targetsGetter
        showNametag(fakePlayer)
        fakePlayer.apply {
            on<Death> {
                schedulerRunLaterAsync(20) { hideNametag(this) }
            }
            on<Respawn> {
                schedulerRunLaterAsync { showNametag(this) }
            }
            config.nametag.refreshEvents.forEach { e ->  on(e) { updateNametag(this) } }
            on<Quit> {
                hideNametag(this)
                nametagTargetsGetters.remove(uniqueId)
                updatingFakePlayers.remove(uniqueId)
            }
        }
    }

    class Listener : org.bukkit.event.Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        fun onPlayerChunkLoad(event: PlayerChunkLoadEvent) {
            val player = event.player
            if (registry.getFakePlayer(player.uniqueId) != null) return
            val chunkKey = getChunkKey(event.chunk.x, event.chunk.z)
            schedulerRunLaterAsync(5) {
                registry.fakeplayersInChunk(chunkKey)?.forEach { fakePlayer ->
                    val targets = nametagTargetsGetters[fakePlayer.uniqueId]?.invoke() ?: return@forEach
                    if (targets.contains(player.uniqueId)) {
                        fakePlayer.showVirtualNametag(player, fillTemplate(fakePlayer))
                    }
                }
            }
        }
    }

}