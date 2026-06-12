package com.coderxi.plugin.fakeplayer

import com.coderxi.plugin.fakeplayer.api.nms.NMSBridge
import com.coderxi.plugin.fakeplayer.api.nms.NMSServer
import com.coderxi.plugin.fakeplayer.command.FakePlayerCommand
import com.coderxi.plugin.fakeplayer.config.PluginConfig
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.event.FakePlayerEventListener
import com.coderxi.plugin.fakeplayer.manager.FakePlayerNametagManager
import com.coderxi.plugin.fakeplayer.manager.FakePlayerRegistry
import com.coderxi.plugin.fakeplayer.manager.FakePlayerTickManager
import com.coderxi.plugin.fakeplayer.nms.v1_21_11.NMSBridgeImpl
import com.coderxi.plugin.fakeplayer.utils.EventEmitter
import com.coderxi.plugin.fakeplayer.utils.Localizer
import com.coderxi.plugin.fakeplayer.utils.PluginConfigUtil
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import revxrsal.commands.bukkit.BukkitLamp

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
        BukkitLamp.builder(this).build().register(FakePlayerCommand())
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
}