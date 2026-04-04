package com.coderxi.plugin.fakeplayer.config

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.*

@Header("FakePlayerPlus 插件配置文件","")
class PluginConfig : OkaeriConfig() {

    @Comment("插件语言设置")
    var language: String = "zh_CN"

    @Comment("假人死亡时动作 NONE|QUIT|RESPAWN|RESPAWN_BACK")
    val onDeathAction: DeathAction = DeathAction.RESPAWN_BACK

}