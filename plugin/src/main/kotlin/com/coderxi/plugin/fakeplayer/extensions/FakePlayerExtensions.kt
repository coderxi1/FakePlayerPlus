package com.coderxi.plugin.fakeplayer.extensions

import com.coderxi.plugin.fakeplayer.manager.FakePlayerOwner
import com.coderxi.plugin.fakeplayer.manager.ConsoleFakePlayerOwner
import com.coderxi.plugin.fakeplayer.manager.PlayerFakePlayerOwner
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

val Player.isFake: Boolean get() = FakePlayerOwner.Registry.getFakePlayer(uniqueId) != null
val Player.hasFake: Boolean get() = FakePlayerOwner.Registry.getOwner(uniqueId) != null
val CommandSender.asFakePlayerOwner: FakePlayerOwner<out CommandSender> get() {
    if (this is Player) {
        return FakePlayerOwner.Registry.getOwner(uniqueId) ?: PlayerFakePlayerOwner(this)
    } else if (this is ConsoleCommandSender){
        return FakePlayerOwner.Registry.getOwner(ConsoleFakePlayerOwner.CONSOLE_UUID) ?: ConsoleFakePlayerOwner(this)
    }
    throw IllegalStateException("Unsupported CommandSender ${this.javaClass.canonicalName} type to FakePlayerOwner")
}
