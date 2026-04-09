package com.coderxi.plugin.fakeplayer.scope

import com.coderxi.plugin.fakeplayer.command.Permission
import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.manager.FakePlayerNametagManager
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player

class PersonalFakePlayerScope(private val player: Player): AbstractFakePlayerScope(player.uniqueId) {

    override fun notify(message: Component) = player.sendMessage(message)

    override fun checkSpawnLimit(): Boolean =
        fakeplayers.size < (Permission.SPAWN_LIMIT_GROUPS.filter { (perm, _) -> player.hasPermission(perm.node) }.values.maxOrNull() ?: config.spawnLimit.default)

    override fun getFakePlayerSpawnLocation(): Location = player.location

    private fun playerAsList() = listOf(player)

    override fun onFakePlayerSpawn(fakePlayer: FakePlayer) {
        fakePlayer.setPing(-1)
        fakePlayer.player.apply {
            isPersistent = true
            isSleepingIgnored = true
            isInvulnerable = false
            isCollidable = true
            canPickupItems = true
            health = 20.0
            foodLevel = 20
        }
        FakePlayerNametagManager.bind(fakePlayer,::playerAsList)
    }

}