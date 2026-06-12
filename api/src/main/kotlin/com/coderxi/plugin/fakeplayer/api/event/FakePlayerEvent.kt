package com.coderxi.plugin.fakeplayer.api.event

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot

interface FakePlayerEvent {

    //假人刚被初始化 (尚未建立网络连接)
    object PreSpawn: FakePlayerEvent
    //假人已建立网络连接并注册到了假人列表 (尚未进入世界)
    object PostSpawn: FakePlayerEvent
    //假人已进入世界
    object AfterSpawn: FakePlayerEvent
    //假人触发了退出事件
    data class Quit(val reason: Component?) : FakePlayerEvent
    //假人退出事件完成之后
    object PostQuit : FakePlayerEvent

    object Respawn: FakePlayerEvent
    data class Death(val location: Location) : FakePlayerEvent

    @JvmInline value class Damage(val finalDamage: Double) : FakePlayerEvent
    @JvmInline value class RegainHealth(val amount: Double) : FakePlayerEvent
    object ExpChange: FakePlayerEvent
    object LevelChange: FakePlayerEvent

    data class Move(val from: Location,val to: Location) : FakePlayerEvent

    data class Interact(val player: Player, val hand: EquipmentSlot) : FakePlayerEvent

}