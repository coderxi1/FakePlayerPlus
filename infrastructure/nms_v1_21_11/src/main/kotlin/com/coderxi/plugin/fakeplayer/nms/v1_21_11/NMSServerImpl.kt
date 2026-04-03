package com.coderxi.plugin.fakeplayer.nms.v1_21_11

import com.coderxi.plugin.fakeplayer.api.nms.NMSServer
import com.coderxi.plugin.fakeplayer.api.nms.NMSServerPlayer
import com.mojang.authlib.GameProfile
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ServerPlayer
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.craftbukkit.CraftServer
import java.util.UUID

class NMSServerImpl(private val server: Server = Bukkit.getServer()) : NMSServer {

    val handle: MinecraftServer = (server as CraftServer).server

    override fun newPlayer(uuid: UUID, name: String): NMSServerPlayer {
        val serverPlayer = ServerPlayer(
            handle,
            NMSServerLevelImpl(Bukkit.getWorlds()[0]).handle,
            GameProfile(uuid, name),
            ClientInformation.createDefault()
        )
        return NMSServerPlayerImpl(serverPlayer.bukkitEntity)
    }
}