package com.coderxi.plugin.fakeplayer.entity

import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer.SkinInfo
import com.coderxi.plugin.fakeplayer.api.nms.*
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.UUID

class StandardFakePlayer(
    override val name: String,
    override val uuid: UUID,
    override var ownerUuids: Collection<UUID> = emptyList(),
    private var _skin: SkinInfo? = null
) : FakePlayer {

    override var skin: SkinInfo?
        get() = _skin;
        set(skin) {
            if (skin == null || skin.textures == null || skin.signature == null) nmsPlayer.setTextures(null, null)
            else nmsPlayer.setTextures(skin.textures!!, skin.signature!!)
            _skin = skin
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
            isInvulnerable = false
            isCollidable = true
            canPickupItems = true
            health = 20.0
            foodLevel = 20
        }
    }

    override var ping: Int
        get() = nmsConnection.ping
        set(value) {nmsConnection.ping = value}

    override fun doTick() = nmsPlayer.doTick()
    override fun requestRespawn() = nmsPlayer.requestRespawn()

    override fun showVirtualNametag(player: Player, content: Component) = nmsPlayer.showVirtualNametag(player, content)
    override fun updateVirtualNametag(player: Player, content: Component) = nmsPlayer.updateVirtualNametag(player, content)
    override fun hideVirtualNametag(player: Player) = nmsPlayer.hideVirtualNametag(player)

}