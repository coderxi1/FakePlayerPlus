package com.coderxi.plugin.fakeplayer.manager

import com.coderxi.plugin.fakeplayer.context.PluginContext
import org.bukkit.scheduler.BukkitTask

object FakePlayerTickManager : PluginContext {

    init {
        onPluginEnable { start() }
        onPluginDisable { stop() }
    }

    private val registry = FakePlayerRegistry

    private var masterTask: BukkitTask? = null

    fun start() {
        if (masterTask != null) return
        masterTask = scheduler.runTaskTimer(plugin, this::run, 0L, 1L)
    }

    private fun run() {
        if (registry.fakeplayersCount <= 0) return
        registry.fakeplayers().forEach { fakePlayer ->
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