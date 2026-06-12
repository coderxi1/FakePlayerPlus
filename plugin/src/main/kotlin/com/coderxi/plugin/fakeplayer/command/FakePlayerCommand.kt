package com.coderxi.plugin.fakeplayer.command

import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.manager.FakePlayerRegistry
import com.coderxi.plugin.fakeplayer.scope.PersonalFakePlayerScope
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.bukkit.annotation.CommandPermission

@Command("fakeplayer","fp")
class FakePlayerCommand: PluginContext {

    @Subcommand("reload")
    @CommandPermission("fakeplayer.reload")
    fun reload(actor: BukkitCommandActor) {
        plugin.onReload()
        actor.sender().sendMessage(tlp("fakeplayer.reload-success"))
    }

    @Subcommand("spawn")
    fun spawn(name: String, actor: BukkitCommandActor) {
        val player = actor.asPlayer()!!
        val scope = FakePlayerRegistry.getScope(player.uniqueId) ?: PersonalFakePlayerScope(player)
        scope.spawnAsync(name, player)
    }

}