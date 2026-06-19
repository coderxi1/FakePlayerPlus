package com.coderxi.plugin.fakeplayer.component

import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.api.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom

class FakePlayerPingUpdater(private val fpm: FakePlayerManager) : PluginComponent {

    private val fakePlayerFirstPings = ConcurrentHashMap<UUID, Int>()

    private val behaviorConfig get() = plugin.config.behavior

    private var pingJitterTask: BukkitTask? = null

    fun start() {
        if (!behaviorConfig.pingJitter || behaviorConfig.pingJitterInterval <= 0) return
        val intervalTicks = (behaviorConfig.pingJitterInterval * 20).toLong()
        pingJitterTask = scheduler.runTaskTimer(plugin, this::run, intervalTicks, intervalTicks)
    }

    fun run() {
        fpm.fakeplayers().forEach{ it.pingJitter() }
    }

    val FakePlayer.firstPing: Int get() = fakePlayerFirstPings.getOrPut(uuid) { ping }

    fun FakePlayer.pingJitter() {
        if (firstPing < 0) return
        val random = ThreadLocalRandom.current()
        val chance = random.nextInt(100)
        var change = 0
        if (chance < 5) { // 5% 概率触发 ±5~8 ms
            change = random.nextInt(5, 9)
        } else if (chance < 15) { // 10% 概率触发 ±3~4 ms
            change = random.nextInt(3, 5)
        } else if (chance < 75) { // 60% 概率触发 ±1~2 ms
            change = random.nextInt(1, 3)
        } // 剩下的 25% 保持不动 (change = 0)
        if (change > 0) {
            if (ping > firstPing) {
                ping -= change
            } else if (ping < firstPing) {
                ping += change
            } else {
                ping += if (random.nextBoolean()) change else -change
            }
            // 边界控制，差距不超过 8
            if (ping > firstPing + 8) ping = firstPing + 8
            if (ping < firstPing - 8) ping = firstPing - 8
            if (ping < 0) ping = 0
        }
    }

}