package com.coderxi.plugin.fakeplayer.nms.v1_21_11

import com.coderxi.plugin.fakeplayer.api.FakePlayerPlusPluginApi.Companion.javaPlugin as plugin
import com.coderxi.plugin.fakeplayer.api.nms.NMSServerPlayer
import com.coderxi.plugin.fakeplayer.server.FakePlayerAdvancements
import com.destroystokyo.paper.profile.ProfileProperty
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.*
import net.minecraft.server.MinecraftServer
import net.minecraft.server.PlayerAdvancements
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ParticleStatus
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.player.ChatVisiblity
import net.minecraft.world.phys.Vec3
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.world.scores.Scoreboard
import net.minecraft.world.scores.Team
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.entity.CraftEntity
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.lang.reflect.Field
import java.nio.file.Paths

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

    private val playerTeam by lazy { PlayerTeam(dummyScoreboard, "${plugin.name}_${player.uniqueId}").apply { players.add(player.name) } }
    private var playerTeamPacket: Packet<*>? = null

    override var dummyNametagVisibility: Boolean
        get() = playerTeam.nameTagVisibility == Team.Visibility.ALWAYS
        set(b) {
            playerTeam.nameTagVisibility = if (b) Team.Visibility.ALWAYS else Team.Visibility.NEVER
            playerTeamPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(playerTeam, true)
        }

    override var dummyCollidable: Boolean
        get() = playerTeam.collisionRule == Team.CollisionRule.ALWAYS
        set(b) {
            player.isCollidable = b
            playerTeam.collisionRule = if (b) Team.CollisionRule.ALWAYS else Team.CollisionRule.NEVER
            playerTeamPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(playerTeam, true)
        }

    override fun dummyNotify(targets: Collection<Player>) {
        if (playerTeamPacket!=null) targets.forEach { target ->  target.sendPacket(playerTeamPacket!!) }
    }

    companion object {
        private val advancementsField: Field? = runCatching { ServerPlayer::class.java.getDeclaredField("advancements").apply { isAccessible = true } }.getOrNull()
        private var NMSServerPlayerImpl.advancements: PlayerAdvancements?
            get() = advancementsField?.get(handle) as? PlayerAdvancements
            set(value) { advancementsField?.set(handle, value) }
        private val NMSServerPlayerImpl.server: MinecraftServer
            get() = handle.level().server
        private fun Player.sendPacket(packet: Packet<*>) {
            (this as CraftPlayer).handle.connection.send(packet)
        }
        private val dummyScoreboard  = Scoreboard()
    }
}
