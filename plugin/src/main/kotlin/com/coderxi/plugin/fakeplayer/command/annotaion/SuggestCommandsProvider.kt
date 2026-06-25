package com.coderxi.plugin.fakeplayer.command.annotaion

import com.coderxi.plugin.fakeplayer.component.FakePlayerSelector.selected
import org.bukkit.Bukkit
import revxrsal.commands.autocomplete.SuggestionProvider
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import java.util.function.Function

class SuggestCommandsProvider : Function<SuggestCommands, SuggestionProvider<BukkitCommandActor>> {
    override fun apply(t: SuggestCommands): SuggestionProvider<BukkitCommandActor> {
        return SuggestionProvider { context ->
            val input = context.input().peekString()
            val player = context.actor().asPlayer() ?: return@SuggestionProvider emptyList()
            val fakePlayer = player.selected?.player ?: return@SuggestionProvider emptyList()
            Bukkit.getCommandMap().knownCommands.values.filter { command ->
                val hasBukkitPerm = command.permission.isNullOrEmpty() || fakePlayer.hasPermission(command.permission!!)
                hasBukkitPerm && command.testPermissionSilent(fakePlayer)
            }.mapNotNull { if (it.name.startsWith(input)) it.name else null }
        }
    }
}