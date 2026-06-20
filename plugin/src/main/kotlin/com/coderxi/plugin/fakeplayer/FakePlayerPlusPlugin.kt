package com.coderxi.plugin.fakeplayer

import com.coderxi.plugin.fakeplayer.api.FakePlayerPlusPluginApi
import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.api.nms.NMSBridge
import com.coderxi.plugin.fakeplayer.api.nms.NMSServer
import com.coderxi.plugin.fakeplayer.command.FakePlayerCommand
import com.coderxi.plugin.fakeplayer.command.parameter.FakePlayerParameterType
import com.coderxi.plugin.fakeplayer.command.exception.FakePlayerCommandExceptionHandler
import com.coderxi.plugin.fakeplayer.config.FakePlayerPlusPluginConfig
import com.coderxi.plugin.fakeplayer.event.FakePlayerEventDispatcher
import com.coderxi.plugin.fakeplayer.api.manager.FakePlayerManager
import com.coderxi.plugin.fakeplayer.command.annotaion.PluginPermissionFactory
import com.coderxi.plugin.fakeplayer.command.annotaion.Select
import com.coderxi.plugin.fakeplayer.command.annotaion.SelectReplacer
import com.coderxi.plugin.fakeplayer.event.FakePlayerBehaviorImplementListener
import com.coderxi.plugin.fakeplayer.event.FakePlayerLifecycleCommandListener
import com.coderxi.plugin.fakeplayer.component.FakePlayerLimiter
import com.coderxi.plugin.fakeplayer.component.FakePlayerNamer
import com.coderxi.plugin.fakeplayer.component.FakePlayerTicker
import com.coderxi.plugin.fakeplayer.manager.FakePlayerManagerImpl
import com.coderxi.plugin.fakeplayer.component.FakePlayerPingUpdater
import com.coderxi.plugin.fakeplayer.manager.FakePlayerSelector
import com.coderxi.plugin.fakeplayer.nms.v1_21_11.NMSBridgeImpl
import com.coderxi.plugin.fakeplayer.utils.ListenerExtensions.registerMyEvents
import com.coderxi.plugin.fakeplayer.utils.Localizer
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.sql2o.Sql2o
import revxrsal.commands.Lamp
import revxrsal.commands.bukkit.BukkitLamp
import java.io.File

class FakePlayerPlusPlugin: FakePlayerPlusPluginApi, JavaPlugin() {

    override lateinit var nms: NMSBridge private set
    override lateinit var nmsServer: NMSServer private set

    lateinit var config : FakePlayerPlusPluginConfig private set
    lateinit var messages : Localizer private set

    lateinit var sql2o: Sql2o private set
    lateinit var lamp: Lamp<*> private set

    override lateinit var fakePlayerManager: FakePlayerManager

    override fun onEnable() {
        nms = NMSBridgeImpl()
        nmsServer = nms.fromServer(server)
        config = ConfigManager.create(FakePlayerPlusPluginConfig::class.java).apply {
            configure { opt ->
                opt.configurer(YamlBukkitConfigurer())
                opt.bindFile(File(dataFolder, "config.yml"))
                opt.removeOrphans(true)
            }
            saveDefaults().load(true)
        }
        messages = Localizer().apply {
            locale(config.language)
        }
        sql2o = Sql2o("jdbc:sqlite:${File(dataFolder, "$name.db").absolutePath}", null, null).also { sql2o ->
            val sqlStatements = classLoader.getResourceAsStream("database/init.sql")!!
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
                .split(";")
                .map { it.trim() }
                .filter { it.isNotBlank() }
            @Suppress("SqlSourceToSinkFlow")
            sql2o.open().use { conn -> sqlStatements.forEach { sql -> conn.createQuery(sql).executeUpdate() } }
        }
        var fakePlayerLimiter : FakePlayerLimiter
        var fakePlayerNamer : FakePlayerNamer
        fakePlayerManager = FakePlayerManagerImpl().also { fpm ->
            FakePlayerTicker(fpm).start()
            FakePlayerEventDispatcher(fpm).registerMyEvents()
            FakePlayerBehaviorImplementListener(fpm).registerMyEvents()
            FakePlayerLifecycleCommandListener(fpm).registerMyEvents()
            fakePlayerLimiter = FakePlayerLimiter(fpm).apply { registerMyEvents() }
            fakePlayerNamer = FakePlayerNamer(fpm)
            FakePlayerPingUpdater(fpm).registerMyEvents()
            FakePlayerSelector.registerMyEvents()
            fpm.registerMyEvents()
        }
        lamp = BukkitLamp.builder(this)
            .permissionFactory(PluginPermissionFactory())
            .annotationReplacer(Select::class.java, SelectReplacer())
            .dependency(FakePlayerManager::class.java,fakePlayerManager)
            .dependency(FakePlayerLimiter::class.java,fakePlayerLimiter)
            .dependency(FakePlayerNamer::class.java,fakePlayerNamer)
            .parameterTypes { parameters -> parameters.addParameterType(FakePlayer::class.java, FakePlayerParameterType()) }
            .exceptionHandler(FakePlayerCommandExceptionHandler())
            .build()
            .apply { register(FakePlayerCommand()) }
    }

    fun onReload() {
        config.load()
        messages.locale(config.language)
        PluginComponent.executeReload()
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)
        PluginComponent.executeDisable()
    }
}