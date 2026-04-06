package com.coderxi.plugin.fakeplayer.manager

import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import org.bukkit.entity.Player

class PlayerFakePlayerOwner(owner: Player): FakePlayerOwner<Player>(owner,owner.uniqueId) {

    override fun spawn(name: String): FakePlayer {
        val player = create(name).also { fakePlayer ->
            fakePlayer.spawn()
            fakePlayer.teleportAsync(owner.location).thenAccept { _ ->
                fakePlayer.lifecycle.onSpawnComplete()
            }
        }
        select(player)
        return player
    }

}