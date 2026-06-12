package com.coderxi.plugin.fakeplayer.provider.invsee

import com.coderxi.plugin.fakeplayer.context.PluginContext
import org.bukkit.entity.Player
import com.lishid.openinv.IOpenInv
import org.bukkit.inventory.InventoryView

class OpenInvInvseeProvider: InvseeProvider, PluginContext {

    val openInvAPI by lazy { plugin.server.pluginManager.getPlugin("OpenInv") as IOpenInv? }

    override fun openInventory(viewer: Player, whom: Player): InventoryView? {
        return openInvAPI?.let { openInv ->
            openInv.openInventory(viewer,openInv.getSpecialInventory(whom, true),false)
        }
    }

}