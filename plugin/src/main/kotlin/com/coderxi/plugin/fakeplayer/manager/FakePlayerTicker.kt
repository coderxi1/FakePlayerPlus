package com.coderxi.plugin.fakeplayer.manager

import com.coderxi.plugin.fakeplayer.context.PluginContext
import org.bukkit.scheduler.BukkitTask

class FakePlayerTicker(private val manager: FakePlayerManager) : PluginContext {

    init { onPluginDisable { stop() } }

    private var masterTask: BukkitTask? = null

    fun start() {
        if (masterTask != null) return
        masterTask = scheduler.runTaskTimer(plugin, this::run, 0L, 1L)
    }

    private fun run() {
        if (manager.getOnlineFakePlayersCount() <= 0) return
        manager.getOnlineFakePlayers().forEach { fakePlayer ->
            try {
                fakePlayer.doTick()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stop() {
        masterTask?.cancel()
        masterTask = null
    }

}