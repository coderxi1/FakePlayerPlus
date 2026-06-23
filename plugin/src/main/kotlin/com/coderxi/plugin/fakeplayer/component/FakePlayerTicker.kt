package com.coderxi.plugin.fakeplayer.component

import com.coderxi.plugin.fakeplayer.api.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import org.bukkit.scheduler.BukkitTask

class FakePlayerTicker(private val fpm: FakePlayerManager) : PluginComponent {

    private var tickerTask: BukkitTask? = null

    init {
        onPluginDisable(0,this::stop)
    }

    fun start() {
        if (tickerTask != null) return
        tickerTask = scheduler.runTaskTimer(plugin, this::tick, 0L, 1L)
    }

    private fun tick() {
        if (fpm.fakeplayersCount() <= 0) return
        fpm.fakeplayers().forEach { fakePlayer ->
            try {
                fakePlayer.nms.doTick()
                fakePlayer.actions.doTick()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stop() {
        tickerTask?.cancel()
        tickerTask = null
    }

}