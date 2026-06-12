package com.coderxi.plugin.fakeplayer.context

import com.coderxi.plugin.fakeplayer.FakePlayerPlusPlugin
import com.coderxi.plugin.fakeplayer.utils.EventEmitter
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.PropertyKey

interface PluginContext {

    val plugin get() = Vars.plugin
    val nms get() = plugin.nms
    val nmsServer get() = plugin.nmsServer
    val config get() = plugin.config
    val logger get() = plugin.logger
    val namespace get() = Vars.namespace
    val scheduler get() = Bukkit.getScheduler()
    fun schedulerRunLaterAsync(delay: Long = 1, action: () -> Unit) = scheduler.runTaskLaterAsynchronously(plugin, action, delay)
    fun tl(@PropertyKey(resourceBundle = "messages.messages") key: String, vararg args: Any) = plugin.messages.translate(key, *args)
    fun tlp(@PropertyKey(resourceBundle = "messages.messages") key: String, vararg args: Any) = plugin.messages.translateWithPrefix(key, *args)
    fun tls(@PropertyKey(resourceBundle = "messages.messages") key: String, vararg args: Any) = plugin.messages.translateStringWithArgs(key, *args)
    // 在子类init{}中使用下列方法时应在plugin主类定义该类
    fun onPluginEnable(priority: Int = 0, action: (Any) -> Unit) = Vars.emitter.registerEvent("Enable",priority,action)
    fun onPluginReload(priority: Int = 0, action: (Any) -> Unit) = Vars.emitter.registerEvent("Reload",priority,action)
    fun onPluginDisable(priority: Int = 0, action: (Any) -> Unit) = Vars.emitter.registerEvent("Disable",priority,action)
    fun registerEvents(listener: Listener) = onPluginEnable { plugin.server.pluginManager.registerEvents(listener,plugin) }

    companion object Vars {
        private val plugin by lazy { JavaPlugin.getPlugin(FakePlayerPlusPlugin::class.java) }
        private val namespace by lazy { NamespacedKey(plugin, "fakeplayer") }
        val emitter = EventEmitter()
    }

}