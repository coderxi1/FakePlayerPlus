package com.coderxi.plugin.fakeplayer.command.parameter

import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.command.exception.FakePlayerCommandException.NotExitsException
import com.coderxi.plugin.fakeplayer.command.exception.FakePlayerCommandException.NotOwnerException
import com.coderxi.plugin.fakeplayer.command.exception.FakePlayerCommandException.NoSelectedException
import com.coderxi.plugin.fakeplayer.command.permission.Permission.ADMIN
import com.coderxi.plugin.fakeplayer.component.FakePlayerSelector.selected
import com.coderxi.plugin.fakeplayer.utils.PluginComponent.Companion.plugin
import com.coderxi.plugin.fakeplayer.utils.hasPermission
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import revxrsal.commands.autocomplete.SuggestionProvider
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.node.ExecutionContext
import revxrsal.commands.parameter.ParameterType
import revxrsal.commands.stream.MutableStringStream

class FakePlayerParameterType : ParameterType<BukkitCommandActor, FakePlayer> {

    private val fpm get() = plugin.fakePlayerManager

    override fun isGreedy() = true

    override fun parse(input: MutableStringStream, context: ExecutionContext<BukkitCommandActor>): FakePlayer? {
        val name = input.readString()
        val sender = context.actor().sender()
        if (sender is ConsoleCommandSender) {
            return fpm.get(name) ?: throw NotExitsException(name)
        }
        if (sender is Player) {
            if (name.isEmpty()) {
                return sender.selected ?: throw NoSelectedException()
            }
            val selected = fpm.get(name) ?: throw NoSelectedException()
            if (!selected.ownerUuids.contains(sender.uniqueId) && !sender.hasPermission(ADMIN)) {
                throw NotOwnerException(selected.name)
            }
            return selected
        }
        return null
    }

    override fun defaultSuggestions(): SuggestionProvider<BukkitCommandActor> {
        return SuggestionProvider { context ->
            if (context.actor().isConsole || context.actor().sender().hasPermission(ADMIN)) {
                return@SuggestionProvider fpm.fakeplayers().map { it -> it.name }
            } else if (context.actor().isPlayer) {
                return@SuggestionProvider fpm.fakeplayersByOwnerUuid(context.actor().asPlayer()!!.uniqueId).map { it -> it.name }
            }
            return@SuggestionProvider listOf()
        }
    }
}