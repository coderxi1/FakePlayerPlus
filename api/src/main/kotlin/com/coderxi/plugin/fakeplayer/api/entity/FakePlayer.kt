package com.coderxi.plugin.fakeplayer.api.entity

import com.coderxi.plugin.fakeplayer.api.nms.NMSServerGamePacketListener
import com.coderxi.plugin.fakeplayer.api.nms.NMSServerPlayer
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.UUID

interface FakePlayer {

    // 基础信息
    val uuid: UUID
    val name: String
    var skin: SkinInfo?
    data class SkinInfo (
        val textures: String?,
        val signature: String?
    )
    var ownerUuids: Collection<UUID>
    val owners get() = ownerUuids.mapNotNull(Bukkit::getPlayer)

    var spawnerUuid: UUID
    val spawner get() = Bukkit.getPlayer(spawnerUuid)!!


    // 完成网络连接时进行的操作
    fun onConnected(nmsPlayer: NMSServerPlayer ,nmsConnection: NMSServerGamePacketListener)

    // 基础属性
    val player: Player
    val world get() = player.world
    val location: Location get() = player.location

    // 额外属性
    var ping: Int

    // 基础功能
    fun doTick()
    fun chat(message: String) = player.chat(message)
    fun quit(cause: String = "") = player.kick(Component.text(cause))
    fun teleportAsync(location: Location) = player.teleportAsync(location)
    fun requestRespawn()

    // 额外功能
    fun showVirtualNametag(player: Player, content: Component)
    fun updateVirtualNametag(player: Player, content: Component)
    fun hideVirtualNametag(player: Player)
}