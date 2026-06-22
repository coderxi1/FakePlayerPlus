package com.coderxi.plugin.fakeplayer.utils

import com.coderxi.plugin.fakeplayer.utils.PluginComponent.Companion.bukkitMainDispatcher
import kotlinx.coroutines.Dispatchers
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

fun Listener.registerEvents(plugin: JavaPlugin = PluginComponent.plugin) {
    plugin.server.pluginManager.registerEvents(this, plugin)
}

val Dispatchers.BukkitMain get() = bukkitMainDispatcher