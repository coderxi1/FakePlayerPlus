package com.coderxi.plugin.fakeplayer.context

import com.coderxi.plugin.fakeplayer.FakePlayerPlusPlugin
import com.coderxi.plugin.fakeplayer.utils.EventEmitter
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.PropertyKey

interface PluginContext {

    val plugin get() = Companion.plugin
    val nms get() = plugin.nms
    val nmsServer get() = plugin.nmsServer
    val config get() = plugin.config
    val logger get() = plugin.logger
    val scheduler get() = Bukkit.getScheduler()
    fun schedulerRunLaterAsync(delay: Long = 1, action: () -> Unit) = scheduler.runTaskLaterAsynchronously(plugin, action, delay)
    fun tl(@PropertyKey(resourceBundle = "messages.messages") key: String, vararg args: Any) = plugin.messages.translate(key, *args)
    fun tlp(@PropertyKey(resourceBundle = "messages.messages") key: String, vararg args: Any) = plugin.messages.translateWithPrefix(key, *args)
    fun tls(@PropertyKey(resourceBundle = "messages.messages") key: String, vararg args: Any) = plugin.messages.translateStringWithArgs(key, *args)
    // 在子类init{}中使用下列方法时应在plugin主类定义该类
    fun onPluginEnable(priority: Int = 0, action: (Any) -> Unit) = plugin.emitter.registerEvent("Enable",priority,action)
    fun onPluginReload(priority: Int = 0, action: (Any) -> Unit) = plugin.emitter.registerEvent("Reload",priority,action)
    fun onPluginDisable(priority: Int = 0, action: (Any) -> Unit) = plugin.emitter.registerEvent("Disable",priority,action)

    companion object {
        val plugin by lazy { JavaPlugin.getPlugin(FakePlayerPlusPlugin::class.java) }
    }

}