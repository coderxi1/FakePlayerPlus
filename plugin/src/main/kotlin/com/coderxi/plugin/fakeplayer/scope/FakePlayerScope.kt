package com.coderxi.plugin.fakeplayer.scope

import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.manager.FakePlayerRegistry
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import java.util.UUID
import java.util.concurrent.CompletableFuture

interface FakePlayerScope {

    val uniqueId: UUID

    val registry get() = FakePlayerRegistry

    fun fakeplayers(): Collection<FakePlayer>

    fun notify(message: Component)

    fun spawnAsync(name: String, sender: CommandSender): CompletableFuture<FakePlayer?>

    fun remove(uuid: UUID)

    fun destroy()

}