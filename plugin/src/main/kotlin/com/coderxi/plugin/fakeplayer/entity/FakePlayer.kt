package com.coderxi.plugin.fakeplayer.entity

import com.coderxi.plugin.fakeplayer.api.nms.*
import com.coderxi.plugin.fakeplayer.listener.FakePlayerLifecycle
import com.coderxi.plugin.fakeplayer.manager.PluginConfigManager.plugin
import net.kyori.adventure.text.Component

class FakePlayer(val handle: NMSServerPlayer,private val network: NMSNetwork) {

    val player = handle.getPlayer()
    val lifecycle = FakePlayerLifecycle(this)
    val ticker = FakePlayerTicker(this)

    fun spawn() {
        lifecycle.onPreSpawn()
        network.apply {
            placeNewPlayer(player)
            getServerGamePacketListener().setPing(-1)
        }
        player.apply {
            isPersistent = true
            isSleepingIgnored = true
            isInvulnerable = false
            isCollidable = true
            canPickupItems = true
            health = 20.0
            foodLevel = 20
        }
        lifecycle.onPostSpawn()
        ticker.runTaskTimer(plugin, 0, 1)
    }

    fun quit(message: String = "quit") {
        player.kick(Component.text(message))
    }

    fun respawn() {
        handle.respawn()
    }

}