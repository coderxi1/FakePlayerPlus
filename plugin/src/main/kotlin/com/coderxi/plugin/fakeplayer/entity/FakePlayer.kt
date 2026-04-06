package com.coderxi.plugin.fakeplayer.entity

import com.coderxi.plugin.fakeplayer.api.nms.*
import com.coderxi.plugin.fakeplayer.listener.FakePlayerLifecycle
import com.coderxi.plugin.fakeplayer.manager.FakePlayerOwner
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture

class FakePlayer(private val handle: NMSServerPlayer,private val network: NMSNetwork) {

    private val player = handle.getPlayer()
    val lifecycle = FakePlayerLifecycle(this)
    val uniqueId = player.uniqueId
    lateinit var owner: FakePlayerOwner<out CommandSender>

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
    }

    fun chat(message: String) {
        player.chat(message)
    }

    fun quit(cause: String = "quit") {
        player.kick(Component.text(cause))
    }

    fun doTick() {
        handle.doTick()
    }

    fun requestRespawn() {
        handle.requestRespawn()
    }

    fun teleportAsync(location: Location): CompletableFuture<Boolean> {
        return player.teleportAsync(location)
    }

}