package com.coderxi.plugin.fakeplayer.entity

import com.coderxi.plugin.fakeplayer.api.config.FakePlayerSettings
import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer.SkinInfo
import com.coderxi.plugin.fakeplayer.api.nms.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

class StandardFakePlayer(
    override val name: String,
    override val uuid: UUID,
    override var ownerUuids: MutableSet<UUID> = mutableSetOf(),
    private var _skin: SkinInfo? = null,
    private var _settings: FakePlayerSettings
) : FakePlayer {

    override lateinit var spawnerUuid: UUID
    override lateinit var spawnerIp: String

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
            player.isCollidable = settings.collidable
            nmsPlayer.dummyCollidable = settings.collidable
            nmsPlayer.dummyNotify(Bukkit.getOnlinePlayers())
            player.canPickupItems = settings.pickupItems
            player.isInvulnerable = settings.invulnerable
            settings.autoReplenish
            _settings = settings
        }

    lateinit var nmsPlayer: NMSServerPlayer
    private lateinit var nmsConnection: NMSServerGamePacketListener
    override val player: Player get() = nmsPlayer.player

    override fun onConnected(nmsPlayer: NMSServerPlayer, nmsConnection: NMSServerGamePacketListener) {
        this.nmsPlayer = nmsPlayer
        this.nmsConnection = nmsConnection
        player.apply {
            isPersistent = true
            isSleepingIgnored = true
            nmsPlayer.dummyCollidable = settings.collidable
            nmsPlayer.dummyNotify(Bukkit.getOnlinePlayers())
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
}