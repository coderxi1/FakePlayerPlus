package com.coderxi.plugin.fakeplayer

import com.coderxi.plugin.fakeplayer.api.nms.NMSBridge
import com.coderxi.plugin.fakeplayer.api.nms.NMSServer
import com.coderxi.plugin.fakeplayer.config.PluginConfig
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.event.FakePlayerEventListener
import com.coderxi.plugin.fakeplayer.manager.FakePlayerNametagManager
import com.coderxi.plugin.fakeplayer.manager.FakePlayerRegistry
import com.coderxi.plugin.fakeplayer.manager.FakePlayerTickManager
import com.coderxi.plugin.fakeplayer.utils.PluginConfigUtil
import com.coderxi.plugin.fakeplayer.nms.v1_21_11.NMSBridgeImpl
import com.coderxi.plugin.fakeplayer.scope.PersonalFakePlayerScope
import com.coderxi.plugin.fakeplayer.utils.EventEmitter
import com.coderxi.plugin.fakeplayer.utils.Localizer
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

class FakePlayerPlusPlugin: JavaPlugin() {

    val import = listOf(
        FakePlayerRegistry,
        FakePlayerNametagManager,
        FakePlayerTickManager,
        FakePlayerEventListener
    )
    val emitter : EventEmitter get() = PluginContext.emitter

    lateinit var nms: NMSBridge private set
    lateinit var nmsServer: NMSServer private set

    lateinit var config : PluginConfig private set
    lateinit var messages : Localizer private set

    override fun onEnable() {
        nms = NMSBridgeImpl()
        nmsServer = nms.fromServer(server)
        config = PluginConfigUtil.load<PluginConfig>("config.yml")
        messages = Localizer()
        messages.locale(config.language)
        emitter.emit("Enable")
    }

    fun onReload() {
        config.load()
        messages.locale(config.language)
        emitter.emit("Reload")
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)
        emitter.emit("Disable")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            return false
        }
        if (args[0] == "reload") {
            onReload()
            return true
        }
        val scope = FakePlayerRegistry.getScope((sender as Player).uniqueId) ?: PersonalFakePlayerScope(sender)
        when (args[0]) {
            "spawn" -> scope.spawnAsync(args[1], sender).exceptionally { e ->
                e.printStackTrace()
                null
            }
        }
        return true
    }

}