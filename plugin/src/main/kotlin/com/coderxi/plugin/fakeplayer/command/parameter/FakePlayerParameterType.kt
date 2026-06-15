package com.coderxi.plugin.fakeplayer.command.parameter

import com.coderxi.plugin.fakeplayer.command.exception.FakePlayerCommandException
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.manager.FakePlayerManager
import revxrsal.commands.annotation.Dependency
import revxrsal.commands.autocomplete.SuggestionProvider
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.node.ExecutionContext
import revxrsal.commands.parameter.ParameterType
import revxrsal.commands.stream.MutableStringStream

class FakePlayerParameterType : ParameterType<BukkitCommandActor, FakePlayer>, PluginContext {

    private val manager get() = PluginContext.plugin.fakePlayerManager

    override fun parse(input: MutableStringStream, context: ExecutionContext<BukkitCommandActor>): FakePlayer? {
        val name = input.readString()
        if (context.actor().isConsole) {
            return manager.get(name) ?: throw FakePlayerCommandException.NotExits(name)
        } else if (context.actor().isPlayer) {
            return manager.get(name) ?: throw FakePlayerCommandException.NotExits(name)
        }
        return null
    }

    override fun defaultSuggestions(): SuggestionProvider<BukkitCommandActor> {
        return SuggestionProvider { context ->
            if (context.actor().isConsole) {
                return@SuggestionProvider manager.getOnlineFakePlayers().map { it -> it.name }
            } else if (context.actor().isPlayer) {
                return@SuggestionProvider manager.getOnlineFakePlayers().map { it -> it.name }
            }
            return@SuggestionProvider listOf()
        }
    }
}