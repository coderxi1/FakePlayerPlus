package com.coderxi.plugin.fakeplayer.config

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.*

class FakePlayerPlusPluginConfig : OkaeriConfig() {

    @Comment("插件语言设置")
    var language: String = "zh_CN"

    @Comment("插件限制设置")
    var limit = LimitConfig()
    class LimitConfig : OkaeriConfig() {
        @Comment("全服创建数量上限")
        @CustomKey("server-spawn")
        var serverSpawn: Int = 999
        @Comment("玩家创建数量上限 (需要权限: fakeplayer.spawn)")
        @CustomKey("player-spawn")
        var playerSpawn: Int = 3
        @Comment("自定义创建数量权限 (需要手动设置玩家/权限组权限: fakeplayer.spawn.limit.<权限名>)")
        @CustomKey("custom-spawn")
        var customSpawn: Map<String, Int> = hashMapOf("vip" to 10)
        @Comment("玩家IP创建数量上限")
        @CustomKey("ip-spawn")
        var ipSpawn: Int = 3
        @Comment("根据服务器TPS动态调整玩家创建数量上限")
        @CustomKey("tps-adaptive")
        var tpsAdaptive = TpsAdaptiveLimitConfig()
        class TpsAdaptiveLimitConfig : OkaeriConfig() {
            @Comment("是否启用此功能")
            var enabled = true
            @Comment("检测间隔 (单位:秒)")
            var interval = 120
            @Comment("检测阈值 (检测TPS低于此值时，将逐步降低假人上限，高于此值则恢复)")
            var threshold = 17.0f
            @Comment("最低假人上限")
            @CustomKey("min-count")
            var minCount = 1
        }
    }

    @Comment("假人名称功能")
    var name = NameConfig()
    class NameConfig : OkaeriConfig() {

        @Comment("创建假人时的名称设置")
        var spawn = NameSpawnConfig()
        class NameSpawnConfig : OkaeriConfig() {
            @Comment("创建假人时未手动设置名称时通过此模板生成", " 变量: 创建者名称{spawner_name} 自增数字{amount}")
            var template = "{spawner_name}_{amount}"
            @Comment("假人名称允许的字符(正则表达式)")
            var pattern = "^[a-zA-Z0-9_]+\\$"
        }

        @Comment("TAB的名称样式")
        @CustomKey("tab")
        var tab = NameTabConfig()
        class NameTabConfig : OkaeriConfig() {
            @Comment("是否启用(如果与其他TAB插件冲突可关闭此功能)")
            var enable = true
            @Comment("名称模板")
            var template = "<gray><italic>{name}"
            @Comment("在ping图标前面的文本")
            @CustomKey("playerlist-objective-fancy-value")
            var playerlistObjectiveFancyValue = "{ping}<#E67E22>🤖"
        }

        @Comment("TAG的名称样式")
        @CustomKey("tag")
        var tag = NameTagConfig()
        class NameTagConfig : OkaeriConfig() {
            @Comment("是否启用")
            var enable = true
            @Comment("名称模板")
            var template = "<gray>{name}</gray>"
            @Comment("是否启用高级名称标签, 支持换行")
            var unlimited = true
            @CustomKey("unlimited-template-lines")
            var unlimitedTemplateLines: List<String> = arrayListOf(
                "血量:{fakeplayer_health_sprites}<red>[{fakeplayer_health}]",
                "等级:<green>{fakeplayer_level} <white>经验:<green>{fakeplayer_expToLevel} <white>状态:{fakeplayer_status_sprites}",
                "{fakeplayer_name}"
            )
        }
    }

    @Comment("假人行为设置")
    var behavior = BehaviorConfig()
    class BehaviorConfig : OkaeriConfig() {
        @Comment("假人背包查看器")
        @CustomKey("invsee-type")
        var invseeType =  InvseeProviderType.VANILLA
        @Comment("假人死亡时动作")
        @CustomKey("death-action")
        var deathAction = DeathEventAction.RESPAWN_BACK
        @Comment("跟随玩家退出")
        @CustomKey("follow-quiting")
        var followQuiting = true
        @Comment("延迟x秒再跟随退出(若玩家在x秒内重新上线则假人不会被删除)")
        @CustomKey("follow-quiting-delay")
        var followQuitingDelay = 30
        @Comment("模拟真实ping抖动")
        @CustomKey("ping-jitter")
        var pingJitter = true
        @Comment("ping值抖动间隔 (单位:秒)")
        @CustomKey("ping-jitter-interval")
        var pingJitterInterval = 3
    }

    @Comment("假人默认设置")
    @CustomKey("default-settings")
    var defaultSettings = FakePlayerDefaultSettings()
    class FakePlayerDefaultSettings : OkaeriConfig() {
        @Comment("是否开启碰撞箱")
        var collidable: Boolean = true
        @Comment("是否允许捡起物品")
        @CustomKey("pickup-items")
        var pickupItems: Boolean = true
        @Comment("是否无敌")
        var invulnerable: Boolean = false
    }

    @Comment(
        "假人生命周期指令绑定",
        " (无前缀)假人自身执行 变量 {uuid} {name} {spawner_uuid} {spawner_name}",
        " [CONSOLE]控制台执行 变量同上",
        " [SPAWNER]创建者执行 变量同上",
        " [OWNERS]全部所有者都会执行 额外变量 {owner_uuid} {owner_name}"
    )
    @CustomKey("lifecycle-commands")
    var lifecycleCommands = LifecycleCommandsConfig()
    class LifecycleCommandsConfig : OkaeriConfig() {
        @Comment("假人刚被初始化 (尚未建立网络连接) "," 此时无法通过假人自身执行(必须带前缀)")
        var preparing: List<String> = arrayListOf(
            "[CONSOLE] /lp user {uuid} parent set bot"
        )
        @Comment("假人已建立网络连接并注册到了假人列表 (尚未进入世界)")
        var connected: List<String> = arrayListOf(
            "/login FAKEPLAYER111"
        )
        @Comment("假人已进入世界")
        var spawned: List<String> = arrayListOf(
            "/tell {spawner_name} 你好，我来帮你挂机啦！"
        )
        @Comment("假人触发退出事件 (仍在世界中)")
        var quit: List<String> = arrayListOf(
            "/tell {spawner_name} 再见，我要退出啦！"
        )
        @CustomKey("post-quit")
        @Comment("假人完全退出"," 此时无法通过假人自身执行(必须带前缀)")
        var quited: List<String> = arrayListOf(
            "[CONSOLE] /tell {spawner_name} 你创建的假人{name}已被移除"
        )
    }

}