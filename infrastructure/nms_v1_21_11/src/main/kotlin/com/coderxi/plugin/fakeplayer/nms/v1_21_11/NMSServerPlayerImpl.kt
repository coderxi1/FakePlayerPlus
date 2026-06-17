package com.coderxi.plugin.fakeplayer.nms.v1_21_11

import com.coderxi.plugin.fakeplayer.api.nms.NMSServerPlayer
import com.coderxi.plugin.fakeplayer.server.FakePlayerAdvancements
import com.destroystokyo.paper.profile.ProfileProperty
import io.netty.buffer.Unpooled
import io.papermc.paper.adventure.PaperAdventure
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.game.*
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.MinecraftServer
import net.minecraft.server.PlayerAdvancements
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ParticleStatus
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.player.ChatVisiblity
import net.minecraft.world.phys.Vec3
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.entity.CraftEntity
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import org.joml.Vector3f
import java.lang.reflect.Field
import java.nio.file.Paths
import java.util.*

class NMSServerPlayerImpl(override val player: Player) : NMSServerPlayer {

    private val handle: ServerPlayer = (player as CraftPlayer).handle

    override val x: Double get() = handle.x
    override val y: Double get() = handle.y
    override val z: Double get() = handle.z
    override var xo: Double get() = handle.xo; set(v) { handle.xo = v }
    override var yo: Double get() = handle.yo; set(v) { handle.yo = v }
    override var zo: Double get() = handle.zo; set(v) { handle.zo = v }
    override var xRot: Float get() = handle.xRot; set(v) { handle.xRot = v }
    override var yRot: Float get() = handle.yRot; set(v) { handle.yRot = v }

    override var xxa: Float get() = handle.xxa; set(v) {handle.xxa=v}
    override var yya: Float get() = handle.yya; set(v) {handle.yya=v}
    override var zza: Float get() = handle.zza; set(v) {handle.zza=v}

    override val tickCount: Int get() = handle.tickCount
    override val onGround: Boolean get() = handle.onGround
    override val isUsingItem: Boolean get() = handle.isUsingItem

    override fun doTick() = handle.doTick()
    override fun absMoveTo(x: Double, y: Double, z: Double, yRot: Float, xRot: Float) = handle.absSnapTo(x, y, z, yRot, xRot)
    override fun setDeltaMovement(vector: Vector) { handle.deltaMovement = Vec3(vector.x, vector.y, vector.z) }
    override fun startRiding(entity: Entity, force: Boolean, triggerEvents: Boolean): Boolean = handle.startRiding((entity as CraftEntity).handle,force,triggerEvents)
    override fun stopRiding() = handle.stopRiding()
    override fun drop(allStack: Boolean) = handle.drop(allStack)
    override fun drop(slot: Int, throwRandomly: Boolean, retainOwnership: Boolean) { handle.drop(handle.inventory.removeItem(slot, handle.inventory.getItem(slot).count), throwRandomly, retainOwnership) }
    override fun jumpFromGround() = handle.jumpFromGround()
    override fun setJumping(jumping: Boolean) { handle.isJumping = jumping }
    override fun requestRespawn() { handle.connection.handleClientCommand(ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN)) }
    override fun requestSwapItemWithOffhand() { handle.connection.handlePlayerAction(ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND,BlockPos(0, 0, 0),Direction.DOWN)) }
    override fun disableAdvancements() { advancements = FakePlayerAdvancements(server.fixerUpper, server.playerList, server.advancements, Paths.get(System.getProperty("java.io.tmpdir")), handle) }
    override fun setupClientOptions() { handle.updateOptions(ClientInformation(
            "en_us",
            Bukkit.getViewDistance(),
            ChatVisiblity.SYSTEM,
            false,
            0x7f,
            HumanoidArm.RIGHT,
            false,
            true,
            ParticleStatus.MINIMAL
    ))}
    override fun setTextures(value: String?, signature: String?) {
        val playerProfile = player.playerProfile
        if (value == null) {
            playerProfile.removeProperty("textures")
        } else {
            playerProfile.setProperty(ProfileProperty("textures", value, signature))
        }
        player.playerProfile = playerProfile
    }

    override fun resetLastActionTime() = handle.resetLastActionTime()

    private var nametagEntityId = net.minecraft.world.entity.Entity.nextEntityId()

    override fun showVirtualNametag(player: Player, content: net.kyori.adventure.text.Component) {
        val loc = player.location
        // 创建TextDisplay实体包
        val addPacket = ClientboundAddEntityPacket(nametagEntityId, UUID.randomUUID(), loc.x, loc.y, loc.z, 0f, 0f, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0.0)
        // TextDisplay元数据包
        val metadataPacket = ClientboundSetEntityDataPacket(nametagEntityId, listOf(
            // 索引 11: 文本位置
            SynchedEntityData.DataValue(11, EntityDataSerializers.VECTOR3, Vector3f(0f, 0.3f, 0f)),
            // 索引 23: 文本内容 (Component)
            SynchedEntityData.DataValue(23, EntityDataSerializers.COMPONENT, PaperAdventure.asVanilla(content)),
            // 索引 25: 背景颜色 (Int) -> 0 为全透明
            SynchedEntityData.DataValue(25, EntityDataSerializers.INT, 0),
            // 索引 15: 看板模式 (Byte) -> 3 为 Center (始终面向玩家)
            SynchedEntityData.DataValue(15, EntityDataSerializers.BYTE, 3.toByte()),
            // 索引 27: 文本设置 (Byte) -> 1 开启阴影效果，效果更像原版 ID
            SynchedEntityData.DataValue(27, EntityDataSerializers.BYTE, 1.toByte())
        ))
        // 骑乘绑定包
        val passengerPacket = ClientboundSetPassengersPacket.STREAM_CODEC.decode(FriendlyByteBuf(Unpooled.buffer()).apply {
            writeVarInt(player.entityId) // 载具ID
            writeVarInt(1)               // 乘客数量
            writeVarInt(nametagEntityId) // 乘客 ID (NameTag)
        })
        // 发送数据包
        handle.connection.send(addPacket)
        handle.connection.send(metadataPacket)
        handle.connection.send(passengerPacket)
    }

    override fun updateVirtualNametag(player: Player, content: net.kyori.adventure.text.Component) {
        val metadataPacket = ClientboundSetEntityDataPacket(nametagEntityId, listOf(
            SynchedEntityData.DataValue(23, EntityDataSerializers.COMPONENT, PaperAdventure.asVanilla(content)),
        ))
        handle.connection.send(metadataPacket)
    }
    override fun hideVirtualNametag(player: Player) {
        val destroyPacket = ClientboundRemoveEntitiesPacket(nametagEntityId)
        handle.connection.send(destroyPacket)
        nametagEntityId = net.minecraft.world.entity.Entity.nextEntityId()
    }

    companion object {
        private val advancementsField: Field? = runCatching { ServerPlayer::class.java.getDeclaredField("advancements").apply { isAccessible = true } }.getOrNull()
        private var NMSServerPlayerImpl.advancements: PlayerAdvancements?
            get() = advancementsField?.get(handle) as? PlayerAdvancements
            set(value) { advancementsField?.set(handle, value) }
        private val NMSServerPlayerImpl.server: MinecraftServer
            get() = handle.level().server
    }
}
