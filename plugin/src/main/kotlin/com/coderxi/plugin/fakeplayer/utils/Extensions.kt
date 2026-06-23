package com.coderxi.plugin.fakeplayer.utils

import com.coderxi.plugin.fakeplayer.utils.PluginComponent.Companion.bukkitMainDispatcher
import kotlinx.coroutines.Dispatchers
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

fun Listener.registerEvents(plugin: JavaPlugin = PluginComponent.plugin) {
    plugin.server.pluginManager.registerEvents(this, plugin)
}

val Dispatchers.BukkitMain get() = bukkitMainDispatcher

val EMPTY_UUID = UUID(0L, 0L)