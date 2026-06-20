package com.coderxi.plugin.fakeplayer.utils

import com.coderxi.plugin.fakeplayer.FakePlayerPlusPlugin
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.PropertyKey
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.CoroutineContext

interface PluginComponent {

    val plugin get() = Companion.plugin
    val scheduler get() = Bukkit.getScheduler()
    fun tl(@PropertyKey(resourceBundle = "messages.messages") key: String, vararg args: Any) = plugin.messages.translate(key, *args)
    fun tlp(@PropertyKey(resourceBundle = "messages.messages") key: String, vararg args: Any) = plugin.messages.translateWithPrefix(key, *args)
    fun tls(@PropertyKey(resourceBundle = "messages.messages") key: String, vararg args: Any) = plugin.messages.translateStringWithArgs(key, *args)
    fun onPluginReload(priority: Int = 0, action: () -> Unit) = synchronized(reloadHandlers) {
        reloadHandlers.add(Hook(action, priority))
        reloadHandlers.sortWith(compareByDescending { it.priority })
    }
    fun onPluginDisable(priority: Int = 0, action: () -> Unit) = synchronized(disableHandlers) {
        disableHandlers.add(Hook(action, priority))
        disableHandlers.sortWith(compareByDescending { it.priority })
    }
    fun asyncRun(action: suspend CoroutineScope.() -> Unit) = coroutineScope.launch(Dispatchers.IO, CoroutineStart.DEFAULT,action)
    suspend fun <T> mainRun(action: () -> T): T = withContext(bukkitDispatcher){action()}

    companion object {
        val plugin by lazy { JavaPlugin.getPlugin(FakePlayerPlusPlugin::class.java) }
        private data class Hook(val action: () -> Unit, val priority: Int)
        private val reloadHandlers = CopyOnWriteArrayList<Hook>()
        private val disableHandlers = CopyOnWriteArrayList<Hook>()
        internal fun executeReload() = synchronized(reloadHandlers) {
            for (i in 0 until reloadHandlers.size) reloadHandlers[i].action()
        }
        internal fun executeDisable() = synchronized(disableHandlers) {
            for (i in 0 until disableHandlers.size) disableHandlers[i].action()
            reloadHandlers.clear()
            disableHandlers.clear()
        }
        private val coroutineScope = CoroutineScope(Dispatchers.Default)
        private val bukkitDispatcher = object : CoroutineDispatcher() {
            override fun dispatch(context: CoroutineContext, block: Runnable) {
                if (Bukkit.isPrimaryThread()) return block.run()
                plugin.server.scheduler.runTask(plugin, block)
            }
        }
    }

}