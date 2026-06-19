package com.coderxi.plugin.fakeplayer.command

import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import com.coderxi.plugin.fakeplayer.api.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.command.annotaion.Select
import com.coderxi.plugin.fakeplayer.manager.FakePlayerSelector.selected
import com.coderxi.plugin.fakeplayer.command.annotaion.PluginPermission as Permission
import com.coderxi.plugin.fakeplayer.command.exception.FakePlayerCommandException.*
import com.coderxi.plugin.fakeplayer.command.permission.Permission.*
import com.coderxi.plugin.fakeplayer.component.FakePlayerLimiter
import com.coderxi.plugin.fakeplayer.provider.invsee.InvseeProvider
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Cooldown
import revxrsal.commands.annotation.Dependency
import revxrsal.commands.annotation.Named
import revxrsal.commands.annotation.Subcommand
import java.util.concurrent.TimeUnit

@Command("fakeplayer","fp")
class FakePlayerCommand: PluginComponent {

    @Dependency
    lateinit var fpm: FakePlayerManager
    @Dependency
    lateinit var fpl: FakePlayerLimiter

    @Subcommand("reload")
    @Permission(RELOAD,ADMIN)
    fun CommandSender.reload() {
        plugin.onReload()
        sendMessage(tlp("fakeplayer.reload.success"))
    }

    @Subcommand("spawn")
    @Permission(SPAWN, BASIC)
    fun Player.spawn(@Named("name") name: String) {
        if (fpm.get(name)!=null) throw SpawnAlreadyExistsException(name)
        if (fpl.isServerLimited()) throw SpawnServerLimitedException()
        if (fpl.isPlayerLimited(this)) throw SpawnPlayerLimitedException()
        if (fpl.isIpLimited(this)) throw SpawnIpLimitedException()
        if (fpl.isTpsAdaptiveLimited(this)) throw SpawnTpsAdaptiveLimitedException()
        if (Bukkit.getOnlinePlayers().find { it.name == name } != null || Bukkit.getOfflinePlayer(name).hasPlayedBefore()) throw SpawnNameAlreadyUsedException(name)
        asyncRun {
            val fakePlayer = fpm.spawnAsync(name, uniqueId, location) ?: return@asyncRun
            val locationText = "%.2f, %.2f, %.2f".format(location.x, location.y, location.z)
            sendMessage(tlp("fakeplayer.spawn.success", name, fakePlayer.world.name, locationText))
            selected = fakePlayer
        }
    }

    @Subcommand("select")
    @Permission(SELECT,BASIC)
    fun Player.select(fakePlayer: FakePlayer) {
        selected = fakePlayer
        sendMessage(tlp("fakeplayer.select.success", fakePlayer.name))
    }

    @Subcommand("remove")
    @Permission(REMOVE,BASIC)
    fun Player.remove(@Select fakePlayer: FakePlayer) {
        fpm.get(fakePlayer.name)?.quit("Removed by $name")
        sendMessage(tlp("fakeplayer.remove.success", fakePlayer.name))
    }

    @Subcommand("invsee")
    @Permission(INVSEE,BASIC)
    fun Player.invsee(@Select fakePlayer: FakePlayer) {
        InvseeProvider.current.openInventory(this,fakePlayer.player)
        playSound(location, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
    }

    @Subcommand("tp")
    @Permission(TP,BASIC)
    fun Player.tp(@Select fakePlayer: FakePlayer) {
        teleportAsync(fakePlayer.location)
    }

    @Subcommand("tphere")
    @Permission(TP,BASIC)
    fun Player.tphere(@Select fakePlayer: FakePlayer) {
        fakePlayer.player.teleportAsync(location)
    }

    @Subcommand("tpswap")
    @Permission(TP,BASIC)
    fun Player.tpswap(@Select fakePlayer: FakePlayer) {
        val playerLocation = location
        teleportAsync(fakePlayer.location)
        fakePlayer.player.teleportAsync(playerLocation)
    }

    @Subcommand("tppos")
    @Permission(TP,BASIC)
    fun Player.tppos(location: Location, @Select fakePlayer: FakePlayer) {
        fakePlayer.player.teleportAsync(location)
    }

    @Subcommand("skin")
    @Permission(SKIN,BASIC)
    @Cooldown(value = 1, unit = TimeUnit.MINUTES)
    fun Player.skin(@Named("name") targetName: String, @Select fakePlayer: FakePlayer) {
        asyncRun {
            fpm.setSkinAsync(fakePlayer, targetName)
            mainRun { fakePlayer.world.playSound(fakePlayer.location, Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1f) }
        }
    }

    @Subcommand("cmd")
    @Permission(CMD,BASIC)
    fun Player.cmd(@Named("command") command: String, @Select fakePlayer: FakePlayer) {
        Bukkit.dispatchCommand(fakePlayer.player, command.removePrefix("/"))
    }

}