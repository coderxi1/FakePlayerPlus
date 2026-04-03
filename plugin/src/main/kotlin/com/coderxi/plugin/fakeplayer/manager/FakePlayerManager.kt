package com.coderxi.plugin.fakeplayer.manager

import com.coderxi.plugin.fakeplayer.api.nms.NMSBridge
import com.coderxi.plugin.fakeplayer.FakePlayerPlus
import com.coderxi.plugin.fakeplayer.utils.InternalAddressGenerator
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.UUID

class FakePlayerManager(private val bridge: NMSBridge) {

    fun spawnAsync(creator: CommandSender, name: String) {
        val server = Bukkit.getServer()
        val plugin = FakePlayerPlus.instance
        val handle = bridge.fromServer(server).newPlayer(UUID.randomUUID(), name)
        val player = handle.getPlayer()
        player.isPersistent = true
        player.isSleepingIgnored = true
        handle.setPlayBefore()
        handle.disableAdvancements(plugin)

        player.isInvulnerable = false
        player.isCollidable = true
        player.canPickupItems = true

        val network = bridge.createNetwork(InternalAddressGenerator.next(),plugin)
        network.placeNewPlayer(server, player)
        player.health = 20.0
        player.foodLevel = 20
        player.teleportAsync((creator as Player).location)

    }
}