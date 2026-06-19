package com.coderxi.plugin.fakeplayer.api.nms

import net.kyori.adventure.text.Component
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.Vector

interface NMSServerPlayer {

    val player: Player
    // 坐标
    val x: Double
    val y: Double
    val z: Double
    // 偏移量
    var xo: Double
    var yo: Double
    var zo: Double
    // 头部角度
    var xRot: Float
    var yRot: Float
    // 坐标移动
    var xxa: Float
    var yya: Float
    var zza: Float

    /** 获取时刻计数, 尽管假人会退出游戏, 但服务器重启前这个值不会重置 */
    val tickCount: Int
    /** 判断是否在地面 */
    val onGround: Boolean
    /** 是否在使用物品 */
    val isUsingItem: Boolean
    /** 执行刻运算 */
    fun doTick()
    /** 移动玩家 */
    fun absMoveTo(x: Double, y: Double, z: Double, yRot: Float, xRot: Float)
    /** 设置相对移动 */
    fun setDeltaMovement(vector: Vector)

    /** 骑乘实体 */
    fun startRiding(entity: Entity, force: Boolean, triggerEvents: Boolean): Boolean
    /** 取消骑乘实体 */
    fun stopRiding()

    /** 丢弃物品 slot 槽位 */
    fun drop(slot: Int, throwRandomly: Boolean, retainOwnership: Boolean)
    /** 丢弃物品 allStack 是否丢弃整组 */
    fun drop(allStack: Boolean): Boolean

    /** 重生 */
    fun requestRespawn()
    /** 交换主副手物品 */
    fun requestSwapItemWithOffhand()

    /** 从地面跳起 */
    fun jumpFromGround()
    /** 设置是否跳跃中 */
    fun setJumping(jumping: Boolean)

    /** 设置不保存成就数据 */
    fun disableAdvancements()
    /** 设置客户端选项 */
    fun setupClientOptions()
    /** 设置皮肤贴图 */
    fun setTextures(value: String?, signature: String?)
    /** 重设最后活跃时间 */
    fun resetLastActionTime()

    // 虚拟名称标签
    fun showVirtualNametag(targets: Collection<Player>, content: Component)
    fun updateVirtualNametag(targets: Collection<Player>, content: Component)
    fun hideVirtualNametag(targets: Collection<Player>)

    // 发送碰撞体积更新包
    fun updateCollidable(targets: Collection<Player>, collidable: Boolean, nametag: Boolean)
}
