package com.coderxi.plugin.fakeplayer.expansion

import com.coderxi.plugin.fakeplayer.api.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class FakePlayerPlaceholderExpansion(private val fpm: FakePlayerManager) : PlaceholderExpansion(), PluginComponent {

    override fun getIdentifier() = "fakeplayer"
    override fun getAuthor() = plugin.pluginMeta.authors.joinToString(",")
    override fun getVersion() = plugin.pluginMeta.version
    fun tryRegister() = if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) register() else false

    override fun onPlaceholderRequest(player: Player, params: String): String? {
        val params = params.lowercase()
        // 全局变量
        if (params == "total") {
            return fpm.fakeplayersCount().toString()
        }
        // 玩家变量
        if (params == "isfake") {
            return fpm.isFake(player.uniqueId).toString()
        }
        // 假人变量
        val fakePlayer = fpm.get(player.uniqueId) ?: return null
        if (params == "spawner") {
            return fakePlayer.spawnerName
        }
        if (params == "actions") {
            val actions = fakePlayer.actions.getActiveActions().values
            val actionsTexts = actions.map { tls("fakeplayer.action."+it.type.name.replace("_","-").lowercase()) }
            return actionsTexts.joinToString(tls("fakeplayer.var.action.separator")).trim()
        }
        return null
    }



}