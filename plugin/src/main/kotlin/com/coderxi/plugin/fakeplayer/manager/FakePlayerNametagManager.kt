package com.coderxi.plugin.fakeplayer.manager

import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent.*
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.utils.MinecraftSpritesUtil
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object FakePlayerNametagManager: PluginContext {

    lateinit var nametagTemplate: Component
    val nametagTargetsGetters = ConcurrentHashMap<UUID, () -> Collection<Player>>()
    val nametagArgsSetters: MutableMap<String, (FakePlayer) -> Component> = mutableMapOf(
        "{fakeplayer_name}" to { fp -> Component.text(fp.name) },
        "{fakeplayer_health_sprites}" to { fp -> MinecraftSpritesUtil.health2heartsSprites(fp.player) },
        "{fakeplayer_health}" to { fp -> Component.text("%.1f".format(fp.player.health))},
        "{fakeplayer_level}" to { fp -> Component.text(fp.player.level) },
        "{fakeplayer_expProgress}" to { fp -> Component.text("${(fp.player.exp * 100).toInt()}%")},
        "{fakeplayer_expToLevel}" to { fp -> Component.text("${(fp.player.exp * fp.player.expToLevel).toInt()}/${fp.player.expToLevel}")},
        "{fakeplayer_status_sprites}" to { Component.empty() } //TODO
    )

    init {
        onPluginEnable { nametagTemplate = miniMessage(config.nametag.lines.joinToString("<br><reset>")) }
        onPluginReload { nametagTemplate = miniMessage(config.nametag.lines.joinToString("<br><reset>")) }
    }

    private fun fillTemplate(fakePlayer: FakePlayer) = nametagArgsSetters.entries.fold(nametagTemplate) { nametag, (key, setter) ->
        nametag.replaceText { it.matchLiteral(key).replacement(setter(fakePlayer)) }
    }

    fun showNametag(fakePlayer: FakePlayer, nametag: Component = fillTemplate(fakePlayer)) {
        nametagTargetsGetters[fakePlayer.uniqueId]?.invoke()?.forEach { fakePlayer.showVirtualNametag(it, nametag) }
    }

    fun updateNametag(fakePlayer: FakePlayer, nametag: Component = fillTemplate(fakePlayer)) {
        nametagTargetsGetters[fakePlayer.uniqueId]?.invoke()?.forEach { fakePlayer.updateVirtualNametag(it, nametag) }
    }

    fun hideNametag(fakePlayer: FakePlayer) {
        nametagTargetsGetters[fakePlayer.uniqueId]?.invoke()?.forEach { fakePlayer.hideVirtualNametag(it) }
    }

    fun bind(fakePlayer: FakePlayer, targetsGetter: () -> Collection<Player>) {
        nametagTargetsGetters[fakePlayer.uniqueId] = targetsGetter
        showNametag(fakePlayer)
        fakePlayer.apply {
            on<Death> {
                schedulerRunLaterAsync(20) { hideNametag(this) }
            }
            on<Respawn> {
                schedulerRunLaterAsync { showNametag(this) }
            }
            on<Damage> {
                schedulerRunLaterAsync { updateNametag(this) }
            }
            on<RegainHealth> {
                schedulerRunLaterAsync { updateNametag(this) }
            }
            on<Quit> {
                hideNametag(this)
                nametagTargetsGetters.remove(uniqueId)
            }
        }
    }

}