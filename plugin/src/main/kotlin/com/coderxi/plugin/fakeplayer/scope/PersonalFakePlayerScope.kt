package com.coderxi.plugin.fakeplayer.scope

import com.coderxi.plugin.fakeplayer.api.event.FakePlayerEvent.Interact
import com.coderxi.plugin.fakeplayer.command.Permission
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.factory.FakePlayerFactory.spawnAsyncNull
import com.coderxi.plugin.fakeplayer.manager.FakePlayerNametagManager
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class PersonalFakePlayerScope(private val player: Player): AbstractFakePlayerScope(player.uniqueId), PluginContext {

    override fun notify(message: Component) = player.sendMessage(message)
    override fun spawnAsync(name: String, sender: CommandSender): CompletableFuture<FakePlayer?> {
        if (fakeplayers.size >= (Permission.SPAWN_LIMIT_GROUPS.filter { (perm, _) -> player.hasPermission(perm.node) }.values.maxOrNull() ?: config.spawnLimit.default)) {
            sender.sendMessage(tlp("fakeplayer.spawn.scope-limit"))
            return spawnAsyncNull
        }
        val uuid = uuid(name)
        if (fakeplayers.containsKey(uuid)) {
            sender.sendMessage(tlp("fakeplayer.spawn.exists",name))
            return spawnAsyncNull
        }
        return forceSpawnAsync(uuid, name, (sender as Player).location) { fakePlayer ->
            val idAsList = listOf(uniqueId)
            FakePlayerNametagManager.bind(fakePlayer) { idAsList }
            fakePlayer.on<Interact> { event ->
                fakePlayer.showInventory(event.player)
            }
        }
    }

}