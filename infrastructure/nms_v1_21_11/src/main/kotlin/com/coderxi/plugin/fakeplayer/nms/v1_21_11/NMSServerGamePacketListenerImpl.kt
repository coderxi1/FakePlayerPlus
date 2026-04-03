package com.coderxi.plugin.fakeplayer.nms.v1_21_11

import com.coderxi.plugin.fakeplayer.api.nms.NMSServerGamePacketListener
import com.coderxi.plugin.fakeplayer.network.FakeConnection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.common.custom.DiscardedPayload
import net.minecraft.network.protocol.game.ClientboundRespawnPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.CommonListenerCookie
import net.minecraft.server.network.ServerGamePacketListenerImpl
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class NMSServerGamePacketListenerImpl(
    server: DedicatedServer,
    connection: FakeConnection,
    handle: ServerPlayer,
    cookie: CommonListenerCookie,
    private val plugin: JavaPlugin
) : ServerGamePacketListenerImpl(server, connection, handle, cookie), NMSServerGamePacketListener {

    override fun send(packet: Packet<*>) {
        when (packet) {
            is ClientboundCustomPayloadPacket -> { handleClientboundCustomPayloadPacket(packet) }
            is ClientboundSetEntityMotionPacket -> { handleClientboundSetEntityMotionPacket(packet) }
            is ClientboundRespawnPacket -> { handleClientboundRespawnPacket(packet) }
        }
    }

    fun handleClientboundCustomPayloadPacket(packet: ClientboundCustomPayloadPacket) {
        val payload = packet.payload()
        val id = payload.type().id()
        if (id.namespace.lowercase() == "bungeecord" && payload is DiscardedPayload) {
            val recipient = Bukkit.getOnlinePlayers().firstOrNull()
            if (recipient == null) {
                plugin.logger.warning("No real player to forward Bungee message: ${id.path}")
                return
            }
            recipient.sendPluginMessage(plugin, "${id.namespace}:${id.path}", payload.data())
        }
    }

    fun handleClientboundSetEntityMotionPacket(packet: ClientboundSetEntityMotionPacket) {
        if (packet.id == player.id && player.hurtMarked) {
            player.bukkitEntity.scheduler.execute(plugin,{
                player.hurtMarked = true
                player.lerpMotion(packet.movement)
            },null,1L)
        }
    }

    fun handleClientboundRespawnPacket(packet: ClientboundRespawnPacket) {
        player.hasChangedDimension()
    }

}