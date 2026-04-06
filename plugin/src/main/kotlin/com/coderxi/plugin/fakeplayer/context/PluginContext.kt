package com.coderxi.plugin.fakeplayer.context

import com.coderxi.plugin.fakeplayer.FakePlayerPlus
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.PropertyKey
import java.util.concurrent.ConcurrentHashMap

interface PluginContext {

    val plugin get() = Vars.plugin
    val bridge get() = plugin.bridge
    val nmsServer get() = Vars.nmsServer
    val config get() = plugin.config
    val logger get() = plugin.logger
    val namespace get() = Vars.namespace
    fun tl(@PropertyKey(resourceBundle = "messages.messages") key: String, vararg args: Any) = plugin.messages.translate(key, *args)

    fun onReload(action: () -> Unit) {
        reloadActions.putIfAbsent(this, action)
    }

    private object Vars {
        val plugin by lazy { JavaPlugin.getPlugin(FakePlayerPlus::class.java) }
        val nmsServer by lazy { plugin.bridge.fromServer(plugin.server) }
        val namespace by lazy { NamespacedKey(plugin, "fakeplayer") }
    }

    companion object {
        private val reloadActions = ConcurrentHashMap<PluginContext, () -> Unit>()
        internal fun emitReload() { reloadActions.values.forEach { it.invoke() } }
    }

}