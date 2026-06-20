package com.coderxi.plugin.fakeplayer.component

import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.api.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class FakePlayerNamer(private val fpm: FakePlayerManager) : PluginComponent.AutoRegister() {

    var spawnPatternRegex: Regex? = null

    override fun onload() {
        spawnPatternRegex = Regex(plugin.config.name.spawnPattern)
    }

    fun checkPattern(name: String): Boolean {
        return spawnPatternRegex!!.matches(name)
    }

    fun nextFakePlayerName(spawner: Player): String? {
        val template = plugin.config.name.spawnTemplate
        if (!template.contains("{amount}")) {
            return template.replace("{spawner_name}", spawner.name)
        }
        val regex = Regex("^" + template
            .replace("{spawner_name}", Regex.escape(spawner.name))
            .replace("{amount}", "(\\d+)") + "$")
        val existingAmounts = fpm.fakeplayersByOwnerUuid(spawner.uniqueId)
            .map(FakePlayer::name)
            .mapNotNull { regex.matchEntire(it)?.groupValues?.get(1)?.toIntOrNull() }
            .toSet()
        var finalName: String? = null
        var i = 0
        while(true) {
            if (existingAmounts.contains(++i)) continue
            val checkName = template.replace("{spawner_name}", spawner.name).replace("{amount}", i.toString())
            if (!Bukkit.getOfflinePlayer(checkName).hasPlayedBefore()
                || fpm.isOwned(spawner.uniqueId, checkName)) {
                finalName = checkName
                break
            }
        }
        return finalName
    }

}