package com.coderxi.plugin.fakeplayer.manager

import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import org.bukkit.Location
import java.util.UUID
import java.util.concurrent.CompletableFuture

interface FakePlayerManager {

    fun getOnlineFakePlayers(): Collection<FakePlayer>

    fun getOnlineFakePlayersCount(): Int

    fun register(fakePlayer: FakePlayer)

    fun unregister(uuid: UUID)

    fun isFakePlayer(uuid: UUID): Boolean

    fun spawnAsync(name: String, location: Location): CompletableFuture<FakePlayer?>

    fun remove(name: String, cause: String = "Removed")

    fun get(uuid: UUID): FakePlayer?

    fun get(name: String): FakePlayer?

    fun checkSpawnLimit(ownerId: UUID): Boolean

}