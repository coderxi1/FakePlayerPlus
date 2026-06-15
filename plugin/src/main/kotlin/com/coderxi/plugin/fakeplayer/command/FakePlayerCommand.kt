package com.coderxi.plugin.fakeplayer.command

import com.coderxi.plugin.fakeplayer.command.annotaion.SelectFlag
import com.coderxi.plugin.fakeplayer.context.PluginContext
import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.provider.invsee.InvseeProvider
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Dependency
import revxrsal.commands.annotation.Named
import revxrsal.commands.annotation.Subcommand

@Command("fakeplayer","fp")
class FakePlayerCommand: PluginContext {

    @Dependency
    lateinit var manager: FakePlayerManager

    @Subcommand("reload")
    fun CommandSender.reload() {
        plugin.onReload()
        sendMessage(tlp("fakeplayer.reload.success"))
    }

    @Subcommand("spawn")
    fun Player.spawn(@Named("name") name: String) {
        if (manager.get(name)!=null) return sendMessage(tlp("fakeplayer.spawn.failed.exists", name))
        if (manager.getOnlineFakePlayersCount()>=config.spawnLimit.server) return sendMessage(tlp("fakeplayer.spawn.failed.server-limit"))
        if (!manager.checkSpawnLimit(uniqueId)) return sendMessage(tlp("fakeplayer.spawn.failed.scope-limit"))
        manager.spawnAsync(name, location).thenAccept { fp ->
            if (fp == null) return@thenAccept
            val locationText = "%.2f, %.2f, %.2f".format(location.x, location.y, location.z)
            sendMessage(tlp("fakeplayer.spawn.success", name, fp.world.name, locationText))
        }
    }

    @Subcommand("select")
    fun Player.select(fakePlayer: FakePlayer) {

    }

    @Subcommand("remove")
    fun CommandSender.remove(@SelectFlag fakePlayer: FakePlayer) {
        fakePlayer.apply { manager.remove(name, "Removed by $name") }
        sendMessage(tlp("fakeplayer.remove.success", fakePlayer.name))
    }

    @Subcommand("invsee")
    fun Player.invsee(@SelectFlag fakePlayer: FakePlayer) {
        InvseeProvider.current.openInventory(this,fakePlayer.player)
        playSound(location, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
    }

    @Subcommand("tp")
    fun Player.tp(@SelectFlag fakePlayer: FakePlayer) {
        teleportAsync(fakePlayer.location)
    }

    @Subcommand("tphere")
    fun Player.tphere(@SelectFlag fakePlayer: FakePlayer) {
        fakePlayer.player.teleportAsync(location)
    }

    @Subcommand("tpswap")
    fun Player.tpswap(@SelectFlag fakePlayer: FakePlayer) {
        val playerLocation = location
        teleportAsync(fakePlayer.location)
        fakePlayer.player.teleportAsync(playerLocation)
    }

    @Subcommand("tppos")
    fun Player.tppos(location: Location, @SelectFlag fakePlayer: FakePlayer) {
        fakePlayer.player.teleportAsync(location)

    }

}