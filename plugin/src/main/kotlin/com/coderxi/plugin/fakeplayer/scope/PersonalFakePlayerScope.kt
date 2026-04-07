package com.coderxi.plugin.fakeplayer.scope

import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import org.bukkit.entity.Player
import java.util.UUID

class PersonalFakePlayerScope(private val player: Player): AbstractFakePlayerScope(player.uniqueId) {

    override fun onFakePlayerSpawn(fakePlayer: FakePlayer) {
        fakePlayer.spawn()
        fakePlayer.teleportAsync(player.location)
    }

    override fun remove(uuid: UUID) {
        TODO("Not yet implemented")
    }

}