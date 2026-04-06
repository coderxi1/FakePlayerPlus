package com.coderxi.plugin.fakeplayer.nms.v1_21_11

import com.coderxi.plugin.fakeplayer.api.nms.NMSServerPlayer
import com.coderxi.plugin.fakeplayer.server.FakePlayerAdvancements
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.NbtOps
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.PlayerAdvancements
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.storage.ValueInputContextHelper
import net.minecraft.world.phys.Vec3
import org.bukkit.craftbukkit.entity.CraftEntity
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector
import java.lang.reflect.Field

class NMSServerPlayerImpl(private val player: Player) : NMSServerPlayer {

    private val handle: ServerPlayer = craftPlayer.handle

    override fun getPlayer() = player

    override fun getX(): Double = handle.x
    override fun getY() = handle.y
    override fun getZ() = handle.z
    override fun getTickCount() = handle.tickCount

    override fun onGround() = handle.onGround()
    override fun isUsingItem() = handle.isUsingItem

    override fun setXo(xo: Double) { handle.xo = xo }
    override fun setYo(yo: Double) { handle.yo = yo }
    override fun setZo(zo: Double) { handle.zo = zo }
    override fun getYRot() = handle.yRot
    override fun setYRot(yRot: Float) { handle.yRot = yRot }
    override fun getXRot() = handle.xRot
    override fun setXRot(xRot: Float) { handle.xRot = xRot }
    override fun getZza() = handle.zza
    override fun setZza(zza: Float) { handle.zza = zza }
    override fun getXxa() = handle.xxa
    override fun setXxa(xxa: Float) { handle.xxa = xxa }

    override fun doTick() = handle.doTick()
    override fun absMoveTo(x: Double, y: Double, z: Double, yRot: Float, xRot: Float) = handle.absSnapTo(x, y, z, yRot, xRot)
    override fun stopRiding() = handle.stopRiding()
    override fun drop(allStack: Boolean) = handle.drop(allStack)
    override fun drop(slot: Int, throwRandomly: Boolean, retainOwnership: Boolean) { handle.drop(handle.inventory.removeItem(slot, handle.inventory.getItem(slot).count), throwRandomly, retainOwnership) }
    override fun resetLastActionTime() = handle.resetLastActionTime()
    override fun jumpFromGround() = handle.jumpFromGround()
    override fun setJumping(jumping: Boolean) { handle.isJumping = jumping }
    override fun setDeltaMovement(vector: Vector) { handle.deltaMovement = Vec3(vector.x, vector.y, vector.z) }
    override fun startRiding(entity: Entity, force: Boolean, triggerEvents: Boolean): Boolean = handle.startRiding((entity as CraftEntity).handle,force,triggerEvents)

    override fun disableAdvancements(plugin: Plugin) { advancements = FakePlayerAdvancements(server.fixerUpper, server.playerList, server.advancements, plugin.dataFolder.getParentFile().toPath(), handle) }

    override fun setPlayBefore() { craftPlayer.readExtraData(ValueInputContextHelper(server.registryAccess(), NbtOps.INSTANCE).empty()) }
    override fun setupClientOptions() { handle.updateOptions(ClientInformation.createDefault())}
    override fun requestRespawn() { handle.connection.handleClientCommand(ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN)) }
    override fun requestSwapItemWithOffhand() { handle.connection.handlePlayerAction(ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND,BlockPos(0, 0, 0),Direction.DOWN)) }

    companion object {
        private val advancementsField: Field? = runCatching { ServerPlayer::class.java.getDeclaredField("advancements").apply { isAccessible = true } }.getOrNull()
        private var NMSServerPlayerImpl.advancements: PlayerAdvancements?
            get() = advancementsField?.get(handle) as? PlayerAdvancements
            set(value) { advancementsField?.set(handle, value) }
        private val NMSServerPlayerImpl.server: MinecraftServer
            get() = handle.level().server
        private val NMSServerPlayerImpl.craftPlayer: CraftPlayer
            get() = player as CraftPlayer
    }
}
