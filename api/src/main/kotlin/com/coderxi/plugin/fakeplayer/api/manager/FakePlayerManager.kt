package com.coderxi.plugin.fakeplayer.api.manager

import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.UUID

interface FakePlayerManager {

    // 假人列表(仅在线)
    fun fakeplayers(): Collection<FakePlayer>

    fun fakeplayersCount(): Int

    fun fakeplayersByOwners(): Map<UUID, Collection<UUID>>

    fun fakeplayersByOwnerUuid(ownerUuid: UUID): Collection<FakePlayer>

    fun get(uuid: UUID): FakePlayer?

    fun get(name: String): FakePlayer?

    // 判断(先查在线 再查数据库) 最好异步执行
    fun isFakePlayer(name: String): Boolean

    fun isOwned(playerUuid: UUID, fakePlayerUuid: UUID): Boolean

    fun isOwned(playerUuid: UUID, fakePlayerName: String): Boolean

    // 操作假人
    suspend fun spawnAsync(name: String, senderUuid: UUID, location: Location): FakePlayer?

    suspend fun spawnAsync(name: String, sender: Player) = spawnAsync(name,sender.uniqueId, sender.location)

    // 持久化保存假人信息 (到数据库) 务必异步执行

    suspend fun saveSkin(fakePlayer: FakePlayer)

    suspend fun saveSettings(fakePlayer: FakePlayer)

}