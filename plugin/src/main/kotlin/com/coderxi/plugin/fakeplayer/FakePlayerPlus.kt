package com.coderxi.plugin.fakeplayer

import com.coderxi.plugin.fakeplayer.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.nms.v1_21_11.NMSBridgeImpl
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class FakePlayerPlus : JavaPlugin() {

    companion object {
        lateinit var instance: FakePlayerPlus private set
        lateinit var manager: FakePlayerManager private set
    }

    override fun onEnable() {
        instance = this
        manager = FakePlayerManager(NMSBridgeImpl())
        logger.info("FakePlayerPlus enabled")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args[0] == "spawn") {
            manager.spawnAsync(sender,args[1])
        }
        return true
    }
}