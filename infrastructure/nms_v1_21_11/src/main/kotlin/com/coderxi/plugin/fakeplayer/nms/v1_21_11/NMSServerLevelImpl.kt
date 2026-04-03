package com.coderxi.plugin.fakeplayer.nms.v1_21_11

import com.coderxi.plugin.fakeplayer.api.nms.NMSServerLevel
import net.minecraft.server.level.ServerLevel
import org.bukkit.World
import org.bukkit.craftbukkit.CraftWorld

class NMSServerLevelImpl(world: World) : NMSServerLevel {

    val handle: ServerLevel = (world as CraftWorld).handle

}