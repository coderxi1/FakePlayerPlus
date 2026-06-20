package com.coderxi.plugin.fakeplayer.manager

import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerConnectedEvent
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerPreparingEvent
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerQuitedEvent
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerSpawnedEvent
import com.coderxi.plugin.fakeplayer.api.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import com.coderxi.plugin.fakeplayer.entity.StandardFakePlayer
import com.coderxi.plugin.fakeplayer.repository.FakePlayerRepository
import com.coderxi.plugin.fakeplayer.utils.IPGenerator
import kotlinx.coroutines.future.await
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.util.UUID

class FakePlayerManagerImpl : FakePlayerManager, PluginComponent, Listener {

    val repository = FakePlayerRepository()
    val registry = FakePlayerRegistry()
    override fun fakeplayers() = registry.fakeplayers.values
    override fun fakeplayersCount() = registry.fakeplayers.count()
    override fun fakeplayersByOwners(): Map<UUID, Collection<UUID>> = registry.fakeplayersByOwnerUuids

    override fun fakeplayersByOwnerUuid(ownerUuid: UUID) = registry.fakeplayersByOwnerUuid(ownerUuid)
    override fun get(uuid: UUID): FakePlayer? = registry.fakeplayers[uuid]
    override fun get(name: String): FakePlayer? = registry.fakeplayersByName[name]

    override fun isFakePlayer(name: String) = get(name)!=null || repository.findByName(name) != null

    private fun uuid(name: String) = UUID.nameUUIDFromBytes("${plugin.name}:$name".toByteArray())

    override suspend fun spawnAsync(name: String, senderUuid: UUID, location: Location): FakePlayer? {
        val fakePlayer = repository.findByName(name) ?: buildAsync(name,senderUuid)
        mainRun {
            fakePlayer.spawnerUuid = senderUuid
            fakePlayer.spawnerIp = Bukkit.getPlayer(senderUuid)?.address?.address?.hostAddress ?: ""
            FakePlayerPreparingEvent(fakePlayer).callEvent()
            val nmsPlayer =  plugin.nmsServer.newPlayer(fakePlayer.uuid, fakePlayer.name).apply {
                disableAdvancements()
                fakePlayer.skin?.let { setTextures(it.textures, it.signature) }
                setupClientOptions()
            }
            registry.register(fakePlayer)
            val nmsNetwork = plugin.nms.createNetwork(IPGenerator.next())
            val nmsConnection = nmsNetwork.placeNewPlayer(nmsPlayer.player)
            fakePlayer.onConnected(nmsPlayer, nmsConnection)
            FakePlayerConnectedEvent(fakePlayer).callEvent()
        }
        val spawned = fakePlayer.teleportAsync(location).await()
        if (!spawned) {
            fakePlayer.quit("Spawn failed")
            return null
        }
        mainRun { FakePlayerSpawnedEvent(fakePlayer).callEvent() }
        return fakePlayer
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private fun unregisterOnQuit(event: FakePlayerQuitedEvent) {
        registry.unregister(event.fakePlayer.uuid)
    }

    private fun buildAsync(name: String, senderUuid: UUID): FakePlayer {
        val fakePlayer = StandardFakePlayer(name,uuid(name),listOf(senderUuid),null, plugin.config.defaultSettings.clone())
        repository.save(fakePlayer, true)
        return fakePlayer
    }

    override fun isOwned(playerUuid: UUID, fakePlayerUuid: UUID): Boolean {
        return registry.fakeplayersByOwnerUuid(playerUuid).find{ it.uuid == fakePlayerUuid } != null
                || repository.findByUuid(fakePlayerUuid)?.ownerUuids?.contains(playerUuid) ?: false
    }

    override fun isOwned(playerUuid: UUID, fakePlayerName: String): Boolean {
        return registry.fakeplayersByOwnerUuid(playerUuid).find{ it.name == fakePlayerName } != null
                || repository.findByName(fakePlayerName)?.ownerUuids?.contains(playerUuid) ?: false
    }

    override suspend fun saveSkin(fakePlayer: FakePlayer) {
        repository.saveSkin(fakePlayer)
    }

    override suspend fun saveSettings(fakePlayer: FakePlayer) {
        repository.saveSettings(fakePlayer)
    }

}