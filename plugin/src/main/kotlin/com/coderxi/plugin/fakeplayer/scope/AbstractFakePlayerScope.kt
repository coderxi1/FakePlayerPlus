package com.coderxi.plugin.fakeplayer.scope

import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.event.FakePlayerEvent
import com.coderxi.plugin.fakeplayer.manager.FakePlayerRegistry
import com.coderxi.plugin.fakeplayer.utils.InetAddressUtil
import org.bukkit.scheduler.BukkitTask
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractFakePlayerScope(override val uniqueId: UUID): FakePlayerScope, PluginContext {

    companion object {
        val ipGenerator = InetAddressUtil.Generator()
        val registry = FakePlayerRegistry
    }

    init {
        registry.registerScope(this)
    }

    protected val fakeplayers = ConcurrentHashMap<UUID, FakePlayer>()

    override fun fakeplayers() = fakeplayers.values

    protected fun uuid(name: String): UUID = UUID.nameUUIDFromBytes("FakePlayer:$uniqueId:$name".toByteArray())

    final override fun spawn(name: String): FakePlayer {
        val uuid = uuid(name)
        fakeplayers[uuid]?.let { return it }

        val nmsPlayer = nmsServer.newPlayer(uuid, name).apply {
            setPlayBefore()
            disableAdvancements(plugin)
        }
        val nmsNetwork = bridge.createNetwork(ipGenerator.next(), plugin)
        return FakePlayer(nmsPlayer, nmsNetwork, this).also {
            onFakePlayerSpawn(it)
            registry.registerFakePlayer(it)
            it.on<FakePlayerEvent.Quit> {
                registry.unregisterFakePlayer(uuid)
            }
        }
    }

    protected abstract fun onFakePlayerSpawn(fakePlayer: FakePlayer)

    private var ticker: BukkitTask? = null
    protected fun startTicker() {
        if (ticker != null) return
        ticker = scheduler.runTaskTimer(plugin, Runnable {
            if (fakeplayers.isEmpty()) {
                stopTicker()
                return@Runnable
            }
            tick()
        }, 0L, 1L)
    }

    protected fun stopTicker() {
        ticker?.cancel()
        ticker = null
    }

    override fun tick() {
        onTick()
        fakeplayers.values.forEach { it.doTick() }
    }

    protected open fun onTick() {}

    override fun remove(uuid: UUID) {
        fakeplayers.remove(uuid)
        registry.getFakePlayer(uuid)?.quit()
    }

    override fun destroy() {
        onDestroy()
        stopTicker()
        fakeplayers.clear()
        registry.unregisterScope(this.uniqueId)
    }

    protected open fun onDestroy() {}
}