package com.coderxi.plugin.fakeplayer.command.parameter

import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.command.exception.FakePlayerCommandException.NotExitsException
import com.coderxi.plugin.fakeplayer.command.exception.FakePlayerCommandException.NotOwnerException
import com.coderxi.plugin.fakeplayer.command.exception.FakePlayerCommandException.NoSelectedException
import com.coderxi.plugin.fakeplayer.manager.FakePlayerSelector.selected
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import revxrsal.commands.autocomplete.SuggestionProvider
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.bukkit.exception.SenderNotPlayerException
import revxrsal.commands.node.ExecutionContext
import revxrsal.commands.parameter.ParameterType
import revxrsal.commands.stream.MutableStringStream

class FakePlayerParameterType : ParameterType<BukkitCommandActor, FakePlayer> {

    override fun isGreedy() = true

    companion object: PluginComponent {
        private val fpm get() = plugin.fakePlayerManager
        fun checked(sender: CommandSender, fakePlayer: FakePlayer) : FakePlayer {
            if (sender !is Player) throw SenderNotPlayerException()
            if (!fpm.isOwned(sender.uniqueId,fakePlayer.uuid)) throw NotOwnerException(fakePlayer.name)
            return fakePlayer
        }
    }

    override fun parse(input: MutableStringStream, context: ExecutionContext<BukkitCommandActor>): FakePlayer? {
        val name = input.readString()
        if (context.actor().isConsole) {
            return fpm.get(name) ?: throw NotExitsException(name)
        } else if (context.actor().isPlayer) {
            val player = context.actor().asPlayer()!!
            val fakePlayer : FakePlayer = if (name.isNotEmpty()) {
                fpm.get(name) ?: throw NotExitsException(name)
            } else {
                player.selected ?: throw NoSelectedException()
            }
            return checked(player, fakePlayer)
        }
        return null
    }

    override fun defaultSuggestions(): SuggestionProvider<BukkitCommandActor> {
        return SuggestionProvider { context ->
            if (context.actor().isConsole) {
                return@SuggestionProvider fpm.fakeplayers().map { it -> it.name }
            } else if (context.actor().isPlayer) {
                return@SuggestionProvider fpm.fakeplayersByOwnerUuid(context.actor().asPlayer()!!.uniqueId).map { it -> it.name }
            }
            return@SuggestionProvider listOf()
        }
    }
}