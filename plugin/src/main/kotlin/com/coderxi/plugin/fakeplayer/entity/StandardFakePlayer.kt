package com.coderxi.plugin.fakeplayer.entity

import com.coderxi.plugin.fakeplayer.api.config.FakePlayerSettings
import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer.SkinInfo
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerSettingsChangedEvent
import com.coderxi.plugin.fakeplayer.api.nms.*
import com.coderxi.plugin.fakeplayer.utils.PluginComponent.Companion.plugin
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

class StandardFakePlayer(
    override val name: String,
    override val uuid: UUID,
    override var ownerUuids: Collection<UUID> = emptyList(),
    private var _skin: SkinInfo? = null,
    private var _settings: FakePlayerSettings = plugin.config.defaultSettings.clone()
) : FakePlayer {

    override lateinit var spawnerUuid: UUID

    override var skin: SkinInfo?
        get() = _skin;
        set(skin) {
            if (skin == null || skin.textures == null || skin.signature == null) nmsPlayer.setTextures(null, null)
            else nmsPlayer.setTextures(skin.textures!!, skin.signature!!)
            _skin = skin
        }

    override var settings: FakePlayerSettings
        get() = _settings
        set(settings) {
            collidable = settings.collidable
            player.canPickupItems = settings.pickupItems
            player.isInvulnerable = settings.invulnerable
            _settings = settings
            FakePlayerSettingsChangedEvent(this).callEvent()
        }

    lateinit var nmsPlayer: NMSServerPlayer
    private lateinit var nmsConnection: NMSServerGamePacketListener
    override val player: Player get() = nmsPlayer.player

    override fun onConnected(nmsPlayer: NMSServerPlayer, nmsConnection: NMSServerGamePacketListener) {
        this.nmsPlayer = nmsPlayer
        this.nmsConnection = nmsConnection
        ping = -1
        player.apply {
            isPersistent = true
            isSleepingIgnored = true
            collidable = settings.collidable
            canPickupItems = settings.pickupItems
            isInvulnerable = settings.invulnerable
            health = 20.0
            foodLevel = 20
        }
    }

    override var ping: Int
        get() = nmsConnection.ping
        set(value) {nmsConnection.ping = value}

    override fun doTick() = nmsPlayer.doTick()
    override fun respawn() = nmsPlayer.requestRespawn()

    override fun showVirtualNametag(targets: Collection<Player>, nametag: Component) = nmsPlayer.showVirtualNametag(targets, nametag)
    override fun updateVirtualNametag(targets: Collection<Player>, nametag: Component) = nmsPlayer.updateVirtualNametag(targets, nametag)
    override fun hideVirtualNametag(targets: Collection<Player>) = nmsPlayer.hideVirtualNametag(targets)

    override var collidable: Boolean
        get() = player.isCollidable
        set(collidable) {
            nmsPlayer.updateCollidable(Bukkit.getOnlinePlayers(), collidable, !plugin.config.name.tag.unlimited)
            player.isCollidable = collidable
        }
}