package com.coderxi.plugin.fakeplayer.api.nms

import org.bukkit.Server
import org.bukkit.entity.Player

interface NMSNetwork {

    fun placeNewPlayer(server: Server,  player: Player): NMSServerGamePacketListener
    fun getServerGamePacketListener(): NMSServerGamePacketListener
}
