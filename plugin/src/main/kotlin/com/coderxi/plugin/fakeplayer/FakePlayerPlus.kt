package com.coderxi.plugin.fakeplayer

import com.coderxi.plugin.fakeplayer.api.nms.NMSBridge
import com.coderxi.plugin.fakeplayer.config.PluginConfig
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.extensions.asFakePlayerOwner
import com.coderxi.plugin.fakeplayer.manager.FakePlayerOwner
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

    override fun onEnable() {
        bridge = NMSBridgeImpl()
        messages = PluginMessageUtil()
        config = PluginConfigManager.load<PluginConfig>("config.yml")
        server.pluginManager.registerEvents(FakePlayerOwner.Listener(),this)
        onReload()
    }

    fun onReload() {
        config.load()
        messages.updateLocale(config.language)
        PluginContext.emitReload()
    }

    override fun onDisable() {
        FakePlayerOwner.Registry.owners().forEach { it.destroy() }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            return false
        }
        if (args[0] == "reload") {
            onReload()
            return true
        }
        sender.asFakePlayerOwner.apply {
            when (args[0]) {
                "spawn" -> spawn(args[1])
                "chat" -> selected.chat(args[1])
                "remove" -> remove(selected)
                "tphere" -> selected.teleportAsync(if (sender is Player) sender.location else server.worlds[0].spawnLocation)
            }
        }
        return true
    }
}