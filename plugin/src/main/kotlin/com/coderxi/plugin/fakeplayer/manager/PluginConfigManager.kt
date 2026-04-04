package com.coderxi.plugin.fakeplayer.manager

import com.coderxi.plugin.fakeplayer.context.PluginContext
import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import java.io.File

object PluginConfigManager: PluginContext {
    inline fun <reified T : OkaeriConfig> load(path: String): T = ConfigManager.create(T::class.java).apply {
        configure { opt ->
            opt.configurer(YamlBukkitConfigurer())
            opt.bindFile(File(plugin.dataFolder, path))
            opt.removeOrphans(true)
        }
        saveDefaults().load(true)
    }
}