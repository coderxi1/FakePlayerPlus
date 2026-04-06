package com.coderxi.plugin.fakeplayer.manager

import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import org.bukkit.command.ConsoleCommandSender
import java.util.UUID

class ConsoleFakePlayerOwner(val console: ConsoleCommandSender): FakePlayerOwner<ConsoleCommandSender>(console,CONSOLE_UUID) {

    companion object {
        val CONSOLE_UUID: UUID = UUID.nameUUIDFromBytes("FakePlayer:Internal:ConsoleFakePlayerOwner".toByteArray())
    }

    override fun spawn(name: String): FakePlayer {
        TODO("Not yet implemented")
    }

}