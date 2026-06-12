package com.coderxi.plugin.fakeplayer.provider.invsee

import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView

interface InvseeProvider {

    fun openInventory(viewer: Player, whom: Player): InventoryView?

}