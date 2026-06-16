package com.coderxi.plugin.fakeplayer.command.exception

import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler
import revxrsal.commands.bukkit.exception.SenderNotPlayerException
import com.coderxi.plugin.fakeplayer.command.exception.FakePlayerCommandException.*

class FakePlayerCommandExceptionHandler : BukkitExceptionHandler(), PluginComponent  {

    @HandleException
    fun handleNotPlayerException(e: SenderNotPlayerException, actor: BukkitCommandActor) {
        actor.sender().sendMessage(tlp("fakeplayer.command.not-player"))
    }

    @HandleException
    fun handleCommandException(e: FakePlayerCommandException, actor: BukkitCommandActor) {
        val message = when (e) {
            is NotExitsException         -> tlp("fakeplayer.command.not-exists", e.name)
            is NotOwnerException         -> tlp("fakeplayer.command.not-owner", e.name)
            is SpawnServerLimitedException -> tlp("fakeplayer.spawn.failed.server-limited")
            is SpawnPlayerLimitedException -> tlp("fakeplayer.spawn.failed.player-limited")
            is SpawnAlreadyExistsException -> tlp("fakeplayer.spawn.failed.already-exists", e.name)
            else -> return
        }
        actor.sender().sendMessage(message)
    }

}