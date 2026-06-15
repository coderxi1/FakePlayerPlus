package com.coderxi.plugin.fakeplayer.provider.invsee

import com.coderxi.plugin.fakeplayer.context.PluginContext
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView

interface InvseeProvider {

    fun openInventory(viewer: Player, whom: Player): InventoryView?

    companion object : PluginContext {
        private var _current: InvseeProvider? = null

        val current: InvseeProvider get() = _current ?: config.invseeProviderType.providerClass.getConstructor().newInstance().also { _current = it }

        init { onPluginReload { _current = null } }
    }

}