package com.coderxi.plugin.fakeplayer.nms.v1_21_11

import ca.spottedleaf.dataconverter.minecraft.MCDataConverter
import ca.spottedleaf.dataconverter.minecraft.datatypes.MCTypeRegistry
import ca.spottedleaf.dataconverter.minecraft.util.Version
import com.coderxi.plugin.fakeplayer.api.nms.NMSServer
import com.coderxi.plugin.fakeplayer.api.nms.NMSServerPlayer
import com.mojang.authlib.GameProfile
import com.mojang.datafixers.DataFixer
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.players.NameAndId
import net.minecraft.util.ProblemReporter
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.storage.LevelStorageSource
import net.minecraft.world.level.storage.PlayerDataStorage
import net.minecraft.world.level.storage.TagValueInput
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.craftbukkit.CraftServer
import sun.misc.Unsafe
import java.io.File
import java.lang.reflect.Field
import java.util.*
import java.util.function.Function
import java.util.function.Supplier


class NMSServerImpl(private val server: Server) : NMSServer {

    val minecraftServer: MinecraftServer = (server as CraftServer).server

    override fun getServer() = server

    override fun newPlayer(uuid: UUID, name: String): NMSServerPlayer {
        val serverPlayer = ServerPlayer(
            minecraftServer,
            NMSServerLevelImpl(Bukkit.getWorlds()[0]).handle,
            GameProfile(uuid, name),
            ClientInformation.createDefault()
        )
        minecraftServer.playerList.playerIo.load(serverPlayer.nameAndId()).ifPresent { nbt ->
            val valueInput = TagValueInput.create(
                ProblemReporter.DISCARDING,
                minecraftServer.registryAccess(),
                nbt
            )
            serverPlayer.load(valueInput)
        }
        return NMSServerPlayerImpl(serverPlayer.bukkitEntity)
    }
}