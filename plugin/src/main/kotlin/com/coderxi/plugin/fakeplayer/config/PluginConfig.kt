package com.coderxi.plugin.fakeplayer.config

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.*

@Header("FakePlayerPlus 插件配置文件","")
class PluginConfig : OkaeriConfig() {

    @Comment("插件语言设置")
    var language: String = "zh_CN"

    @Comment("假人创建数量上限")
    @CustomKey("spawn-limit")
    val spawnLimit = SpawnLimit()
    class SpawnLimit : OkaeriConfig() {
        @Comment("全服最大假人总数")
        var server: Int = 999
        @Comment("默认的数量上限 (权限节点: fakeplayer.spawn)")
        var default: Int = 3
        @Comment("自定义权限组的数量上限 (需手动给权限组权限节点: fakeplayer.spawn.limit.<group>)")
        var groups: Map<String, Int> = hashMapOf(
            "scientist" to 10
        )
    }

    @Comment("假人自定义名称")
    val nametag = NametagConfig()
    class NametagConfig : OkaeriConfig() {
        val lines: List<String> = listOf(
            "血量:{fakeplayer_health_sprites}<red>[{fakeplayer_health}]",
            "等级:<green>{fakeplayer_level} <white>经验:<green>{fakeplayer_expToLevel} <white>状态:{fakeplayer_status_sprites}",
            "{fakeplayer_name}"
        )
    }

    @Comment("假人死亡时动作 NONE|QUIT|RESPAWN|RESPAWN_BACK")
    @CustomKey("on-death-action")
    val onDeathAction: OnDeathAction = OnDeathAction.RESPAWN_BACK


}