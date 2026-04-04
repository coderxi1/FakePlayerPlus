package com.coderxi.plugin.fakeplayer.manager

import com.coderxi.plugin.fakeplayer.config.DeathAction
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.listener.FakePlayerLifecycle
import com.coderxi.plugin.fakeplayer.utils.InetAddressUtil
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class FakePlayerManager : PluginContext {

    val listener = Listener(this).apply { plugin.server.pluginManager.registerEvents(this, plugin) }

    private val ipGen = InetAddressUtil.Generator()
    private val fakeplayers = ConcurrentHashMap<UUID, FakePlayer>()

    fun getFakePlayer(uuid: UUID): FakePlayer? {
        return fakeplayers[uuid]
    }

    fun spawn(spawnLocation: Location): FakePlayer {
        val uuid = UUID.randomUUID()
        val name = uuid.toString().split("-")[0]

        val nmsPlayer = nmsServer.newPlayer(uuid, name).apply {
            setPlayBefore()
            disableAdvancements(plugin)
        }
        val nmsNetwork = bridge.createNetwork(ipGen.next(),plugin)
        val player = FakePlayer(nmsPlayer,nmsNetwork).apply {
            lifecycle.addEventListener(FakePlayerLifecycle.Phase.AFTER_QUIT) { fakeplayers.remove(uuid) }
            spawn()
            fakeplayers[uuid] = this
            player.teleportAsync(spawnLocation).thenAccept { success ->
                if (success) {
                    lifecycle.onSpawnComplete()
                }
            }
        }
        return player
    }

    class Listener(val manager: FakePlayerManager): org.bukkit.event.Listener, PluginContext {
        @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
        fun onFakePlayerPostQuit(event: PlayerQuitEvent) {
            manager.getFakePlayer(event.player.uniqueId)?.apply { lifecycle.onPostQuit() }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        fun onFakePlayerAfterQuit(event: PlayerQuitEvent) {
            manager.getFakePlayer(event.player.uniqueId)?.apply {
                Bukkit.getScheduler().runTaskLater(plugin, Runnable { lifecycle.onAfterQuit() }, 1)
            }
        }
        private val pendingRespawn = ConcurrentHashMap<UUID, Location>()
        @EventHandler
        fun onFakePlayerDeath(event: PlayerDeathEvent) {
            manager.getFakePlayer(event.player.uniqueId)?.apply {
                val action = Runnable {
                    when (config.onDeathAction) {
                        DeathAction.NONE -> {}
                        DeathAction.QUIT -> quit("You died")
                        DeathAction.RESPAWN -> respawn()
                        DeathAction.RESPAWN_BACK -> { pendingRespawn[event.player.uniqueId] = event.player.location; respawn() }
                    }
                }
                Bukkit.getScheduler().runTaskLater(plugin, action, 20)
            }
        }
        @EventHandler
        fun onFakePlayerRespawn(event: PlayerRespawnEvent) {
            pendingRespawn[event.player.uniqueId]?.let { loc ->
                event.player.teleportAsync(loc).thenAccept { success -> if (success) {
                    pendingRespawn.remove(event.player.uniqueId)
                }}
            }
        }
    }

}