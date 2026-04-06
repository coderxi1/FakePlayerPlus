package com.coderxi.plugin.fakeplayer.manager

import com.coderxi.plugin.fakeplayer.config.OnDeathAction
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.listener.FakePlayerLifecycle
import com.coderxi.plugin.fakeplayer.utils.InetAddressUtil
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.scheduler.BukkitTask
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

abstract class FakePlayerOwner<T: CommandSender>(protected val owner: T, val ownerId: UUID): PluginContext{

    object Factory: PluginContext {
        private val ipGenerator = InetAddressUtil.Generator()
        fun create(uuid: UUID, name: String): FakePlayer {
            val nmsPlayer = nmsServer.newPlayer(uuid, name).apply {
                setPlayBefore()
                disableAdvancements(plugin)
            }
            val nmsNetwork = bridge.createNetwork(ipGenerator.next(), plugin)
            return FakePlayer(nmsPlayer, nmsNetwork)
        }
    }

    object Registry: PluginContext {
        typealias IFakePlayerOwner = FakePlayerOwner<out CommandSender>
        private val owners = ConcurrentHashMap<UUID, IFakePlayerOwner>()
        private val fakeplayers = ConcurrentHashMap<UUID, FakePlayer>()
        fun owners(): Collection<IFakePlayerOwner> = owners.values
        fun fakeplayers(): Collection<FakePlayer> = fakeplayers.values
        fun getOwner(uuid: UUID): IFakePlayerOwner? = owners[uuid]
        fun getFakePlayer(uuid: UUID) = fakeplayers[uuid]
        val fakePlayerCount get() = fakeplayers.size
        fun registerOwner(owner: IFakePlayerOwner) {owners[owner.ownerId] = owner}
        fun registerFakePlayer(fakePlayer: FakePlayer) {fakeplayers[fakePlayer.uniqueId] = fakePlayer}
        fun unregisterOwner(uuid: UUID) {owners.remove(uuid)}
        fun unregisterFakePlayer(uuid: UUID) {fakeplayers.remove(uuid)}
    }

    class Listener: org.bukkit.event.Listener, PluginContext {
        @EventHandler
        fun onPlayerQuitEvent(event: PlayerQuitEvent) = Registry.getOwner(event.player.uniqueId)?.destroy()
        @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
        fun onFakePlayerPostQuit(event: PlayerQuitEvent) {
            Registry.getFakePlayer(event.player.uniqueId)?.lifecycle?.onPostQuit()
        }
        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        fun onFakePlayerAfterQuit(event: PlayerQuitEvent) {
            Registry.getFakePlayer(event.player.uniqueId)?.apply {
                owner.remove(uniqueId)
                pendingRespawn.remove(uniqueId)
                Bukkit.getScheduler().runTaskLater(plugin, lifecycle::onAfterQuit, 1)
            }
        }
        private val pendingRespawn = ConcurrentHashMap<UUID, Location>()
        @EventHandler
        fun onFakePlayerDeath(event: PlayerDeathEvent) {
            Registry.getFakePlayer(event.player.uniqueId)?.also { it ->
                when (config.onDeathAction) {
                    OnDeathAction.NONE -> null
                    OnDeathAction.QUIT -> Runnable { it.quit("You died") }
                    OnDeathAction.RESPAWN -> Runnable { it.requestRespawn() }
                    OnDeathAction.RESPAWN_BACK -> Runnable {
                        pendingRespawn[event.player.uniqueId] = event.player.location
                        it.requestRespawn()
                    }
                }?.apply { Bukkit.getScheduler().runTaskLater(plugin, this, 20) }
            }
        }
        @EventHandler
        fun onFakePlayerRespawn(event: PlayerRespawnEvent) {
            pendingRespawn[event.player.uniqueId]?.let { loc ->
                event.respawnLocation = loc
                pendingRespawn.remove(event.player.uniqueId)
            }
        }
    }

    val registry get() = Registry
    init { registry.registerOwner(this) }

    private val fakeplayers = ConcurrentHashMap<UUID, FakePlayer>()
    private var fakeplayersTicker: BukkitTask? = null

    fun uuidByName(name: String): UUID = UUID.nameUUIDFromBytes("FakePlayer:${ownerId}:$name".toByteArray())

    lateinit var selected: FakePlayer private set

    fun select(fakePlayer: FakePlayer) = select(fakePlayer.uniqueId)
    fun select(name: String) = select(uuidByName(name))
    fun select(uuid: UUID) = fakeplayers[uuid]?.also { selected = it }


    protected fun create(name: String): FakePlayer {
        val uuid = uuidByName(name)
        return Factory.create(uuid, name).also {
            it.owner = this
            it.lifecycle.addEventListener(FakePlayerLifecycle.Phase.POST_QUIT) {
                remove(uuid)
            }
            if (fakeplayers.isEmpty()){
                startTicker()
            }
            registry.registerFakePlayer(it)
            fakeplayers[uuid] = it
        }
    }

    private fun startTicker() {
        if (fakeplayersTicker != null) return
        fakeplayersTicker = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            fakeplayers.values.forEach { player -> player.doTick() }
        }, 0L, 1)
    }

    fun remove(fakePlayer: FakePlayer) = remove(fakePlayer.uniqueId)
    fun remove(name: String, cause: String = "Owner remove") = remove(uuidByName(name),cause)
    fun remove(uuid: UUID, cause: String = "Owner remove") {
        fakeplayers.remove(uuid)?.also {
            it.quit(cause)
            registry.unregisterFakePlayer(uuid)
            if (fakeplayers.isEmpty()) {
                cleanup()
            }
        }
    }

    fun destroy() = removeAll()

    fun removeAll() {
        val players = fakeplayers.values.toList()
        fakeplayers.clear()
        players.forEach { it.quit("Owner destroyed") }
        cleanup()
    }

    protected fun cleanup() {
        fakeplayersTicker?.cancel()
        fakeplayersTicker = null
        registry.unregisterOwner(ownerId)
    }

    abstract fun spawn(name: String): FakePlayer

}