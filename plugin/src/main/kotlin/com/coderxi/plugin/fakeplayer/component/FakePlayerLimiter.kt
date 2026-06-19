package com.coderxi.plugin.fakeplayer.component

import com.coderxi.plugin.fakeplayer.api.event.FakePlayerConnectedEvent
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerQuitedEvent
import com.coderxi.plugin.fakeplayer.api.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.command.permission.Permission
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.ConcurrentHashMap

class FakePlayerLimiter(private val fpm: FakePlayerManager) : PluginComponent, Listener {

    private val limit get() = plugin.config.limit

    private var tpsLimit = limit.playerSpawn
    private var tpsLimitTask: BukkitTask? = null

    private val ip2Count = ConcurrentHashMap<String, Int>()

    init {
        onPluginReload(0, this::start)
        onPluginDisable(0, this::stop)
    }

    fun isServerLimited(): Boolean {
        return fpm.fakeplayersCount() >= limit.serverSpawn
    }

    fun isPlayerLimited(player: Player): Boolean {
        val playerLimit = limit.customSpawn.filter { (node) ->
            player.hasPermission(Permission.SPAWN_LIMIT_CUSTOM.value.replace("{node}",node))
        }.values.maxOrNull() ?: limit.playerSpawn
        return fpm.fakeplayersByOwnerUuid(player.uniqueId).count() >= playerLimit
    }

    fun isTpsAdaptiveLimited(player: Player): Boolean {
        return fpm.fakeplayersByOwnerUuid(player.uniqueId).count() >= tpsLimit
    }

    @EventHandler
    private fun ip2CoundIncrement(event: FakePlayerConnectedEvent) {
        val ip = event.fakePlayer.spawner.address?.address?.hostAddress ?: return
        ip2Count.merge(ip, 1) { old, new -> old + new }
    }

    @EventHandler
    private fun ip2CoundDecrement(event: FakePlayerQuitedEvent) {
        val ip = event.fakePlayer.spawner.address?.address?.hostAddress ?: return
        ip2Count.computeIfPresent(ip) { _, count -> if (count <= 1) null else count - 1}
    }

    fun isIpLimited(player: Player): Boolean {
        val ip = player.address?.address?.hostAddress ?: return false
        return (ip2Count[ip] ?: 0) >= limit.ipSpawn
    }

    fun start() {
        val tpsAdaptive = limit.tpsAdaptive
        if (!tpsAdaptive.enabled) return
        tpsLimitTask?.cancel()
        val intervalTicks = tpsAdaptive.interval.toLong() * 20
        tpsLimitTask = scheduler.runTaskTimer(plugin, this::run, intervalTicks, intervalTicks)
    }

    private fun run() {
        val tps = plugin.server.tps.first()
        if (tps >= limit.tpsAdaptive.threshold) {
            if (tpsLimit < limit.playerSpawn) {
                tpsLimit++
                plugin.server.broadcast(tlp("fakeplayer.tps-adaptive.limit-recovered", tps, tpsLimit))
            }
            return
        }
        if (tpsLimit <= limit.tpsAdaptive.minCount){
            return
        }
        tpsLimit--
        plugin.server.broadcast(tlp("fakeplayer.tps-adaptive.limit-decreased", tps, tpsLimit))
        fpm.fakeplayersByOwners().forEach { (ownerUuid, fakePlayerUuids) ->
            val overflowCount = fakePlayerUuids.count() - tpsLimit
            if (overflowCount <= 0) return@forEach
            fakePlayerUuids.take(overflowCount).mapNotNull(fpm::get).forEach { fakePlayer ->
                fakePlayer.quit("Tps Adaptive Limit")
            }
        }
    }

    fun stop() {
        tpsLimitTask?.cancel()
        tpsLimitTask = null
    }

}
