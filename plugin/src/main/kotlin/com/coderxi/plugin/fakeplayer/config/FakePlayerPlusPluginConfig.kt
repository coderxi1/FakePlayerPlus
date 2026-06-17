package com.coderxi.plugin.fakeplayer.config

import com.coderxi.plugin.fakeplayer.provider.invsee.InvseeProvider
import com.coderxi.plugin.fakeplayer.provider.invsee.OpenInvInvseeProvider
import com.coderxi.plugin.fakeplayer.provider.invsee.VanillaInvseeProvider
import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.*

class FakePlayerPlusPluginConfig : OkaeriConfig() {

    @Comment("插件语言设置")
    var language: String = "zh_CN"

    @Comment("假人创建数量上限")
    @CustomKey("spawn-limit")
    var spawnLimit = SpawnLimit()
    class SpawnLimit : OkaeriConfig() {
        @Comment("全服假人数量上限")
        var server: Int = 999
        @Comment("玩家默认数量上限 (权限: fakeplayer.spawn)")
        var default: Int = 3
        @Comment("自定义数量上限 (需手动给玩家/权限组权限: fakeplayer.spawn.limit.<group>)")
        var groups: Map<String, Int> = hashMapOf(
            "scientist" to 10
        )
    }

    @CustomKey("invsee")
    var invsee: InvseeProviderType = InvseeProviderType.VANILLA
    enum class InvseeProviderType(val providerClass: Class<out InvseeProvider>) {
        VANILLA(VanillaInvseeProvider::class.java),
        OPENINV(OpenInvInvseeProvider::class.java)
    }

    @Comment("假人死亡事件")
    @CustomKey("on-death-event")
    var onDeathEvent = DeathEventHooks()
    class DeathEventHooks : OkaeriConfig() {
        @Comment("假人死亡时动作")
        var action = DeathEventAction.RESPAWN_BACK
        enum class DeathEventAction { NONE, QUIT, RESPAWN, RESPAWN_BACK }
    }

}