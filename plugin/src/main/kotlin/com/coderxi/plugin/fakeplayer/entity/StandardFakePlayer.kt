package com.coderxi.plugin.fakeplayer.entity

import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.api.nms.*
import com.coderxi.plugin.fakeplayer.provider.invsee.InvseeProvider.Companion.plugin
import com.destroystokyo.paper.profile.ProfileProperty
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.CompletableFuture

class StandardFakePlayer(
    override val name: String,
    override val uuid: UUID,
    override var ownerUuids: Collection<UUID> = emptyList(),
    override var skin: FakePlayer.SkinInfo? = null
) : FakePlayer {

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

    override fun setSkinAsync(targetName: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val targetProfile = Bukkit.createProfile(null, targetName)
        CompletableFuture.supplyAsync(targetProfile::complete).thenApply { success ->
            if (!success) return@thenApply future.complete(false)
            Bukkit.getScheduler().runTask(plugin, Runnable {
                val playerProfile = player.playerProfile
                playerProfile.setTextures(targetProfile.textures)
                targetProfile.properties.find { it.name == "textures" } ?.let { texturesProperty ->
                    skin = FakePlayer.SkinInfo(texturesProperty.value,texturesProperty.signature)
                    playerProfile.setProperty(ProfileProperty("textures",texturesProperty.value))
                    playerProfile.setProperty(ProfileProperty("signature",texturesProperty.signature!!))
                }
                player.playerProfile = playerProfile
                future.complete(true)
            })
        }.exceptionally(future::completeExceptionally)
        return future
    }

}