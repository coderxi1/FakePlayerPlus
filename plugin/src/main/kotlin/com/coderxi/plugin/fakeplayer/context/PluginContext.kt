package com.coderxi.plugin.fakeplayer.context

import com.coderxi.plugin.fakeplayer.FakePlayerPlus
import com.coderxi.plugin.fakeplayer.config.PluginConfig
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

interface PluginContext {

    val plugin get() = Vars.plugin
    val bridge get() = plugin.bridge
    val nmsServer get() = Vars.nmsServer
    val config: PluginConfig get() = plugin.config
    val logger get() = plugin.logger
    val namespace get() = Vars.namespace
    val tl get() = plugin.messages::translate

    private object Vars: PluginContext {
        override val plugin by lazy { JavaPlugin.getPlugin(FakePlayerPlus::class.java) }
        override val nmsServer by lazy { bridge.fromServer(plugin.server) }
        override val namespace by lazy { NamespacedKey(plugin, "fakeplayer") }
    }

}