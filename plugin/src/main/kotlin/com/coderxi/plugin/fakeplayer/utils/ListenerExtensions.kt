package com.coderxi.plugin.fakeplayer.utils

import com.coderxi.plugin.fakeplayer.context.PluginContext
import org.bukkit.event.Listener

object ListenerExtensions: PluginContext {

    fun Listener.register() {
        plugin.server.pluginManager.registerEvents(this,plugin)
    }

}