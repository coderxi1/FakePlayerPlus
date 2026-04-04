package com.coderxi.plugin.fakeplayer

import com.coderxi.plugin.fakeplayer.api.nms.NMSBridge
import com.coderxi.plugin.fakeplayer.config.PluginConfig
import com.coderxi.plugin.fakeplayer.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.manager.PluginConfigManager
import com.coderxi.plugin.fakeplayer.nms.v1_21_11.NMSBridgeImpl
import com.coderxi.plugin.fakeplayer.utils.PluginMessageUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class FakePlayerPlus : JavaPlugin() {

    lateinit var bridge: NMSBridge private set
    lateinit var config : PluginConfig private set
    lateinit var messages : PluginMessageUtil private set
    private lateinit var fakePlayerManager: FakePlayerManager

    override fun onEnable() = onReload(true)

    fun onReload(init: Boolean = false) {
        if (init) {
            bridge = NMSBridgeImpl()
            messages = PluginMessageUtil()
            fakePlayerManager = FakePlayerManager()
        }
        config = PluginConfigManager.load<PluginConfig>("config.yml")
        messages.updateLocale(config.language)
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.isEmpty()) return true
        when (args[0]) {
            "spawn" ->  fakePlayerManager.spawn((sender as Player).location)
            "reload" -> onReload()
            "test" -> sender.sendMessage(messages.translate("fakeplayer.no-permission"))
        }
        return true
    }
}