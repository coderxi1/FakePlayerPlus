package com.coderxi.plugin.fakeplayer.command.exception

import com.coderxi.plugin.fakeplayer.context.PluginContext
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler
import revxrsal.commands.bukkit.exception.SenderNotPlayerException

class FakePlayerCommandExceptionHandler : BukkitExceptionHandler(), PluginContext  {

    @HandleException
    fun ondNotPlayer(e: SenderNotPlayerException, actor: BukkitCommandActor) {
        actor.sender().sendMessage(tlp("fakeplayer.command.not-player"))
    }

    @HandleException
    fun ondNotExits(e: FakePlayerCommandException.NotExits, actor: BukkitCommandActor) {
        actor.sender().sendMessage(tlp("fakeplayer.command.not-exists",e.name))
    }



}