package com.coderxi.plugin.fakeplayer.entity

import com.coderxi.plugin.fakeplayer.context.PluginContext
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.atomic.AtomicBoolean

class FakePlayerTicker(private val that: FakePlayer) : BukkitRunnable(), PluginContext {

    private val firstTick = AtomicBoolean(true)

    override fun run() {
        if (!that.player.isOnline) return cancel()
        try {
            if (firstTick.getAndSet(false)) doFirstTick() else doTick()
        } catch (e: Exception) {
            logger.warning("Error while ticking: ${e.message}")
            cancel()
        }
    }

    private fun doFirstTick() {
        val handle = that.handle
        val player = that.player
        val x = handle.getX()
        val y = handle.getY()
        val z = handle.getZ()
        handle.setXo(x)
        handle.setYo(y)
        handle.setZo(z)
        handle.doTick()
        player.teleport(Location(player.world, x, y, z, player.location.yaw, player.location.pitch))
        handle.absMoveTo(x, y, z, player.location.yaw, player.location.pitch)
    }

    private fun doTick() {
        that.handle.doTick()
    }
}