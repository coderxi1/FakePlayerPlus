package com.coderxi.plugin.fakeplayer.utils

import com.coderxi.plugin.fakeplayer.context.PluginContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.jetbrains.annotations.PropertyKey
import java.io.File
import java.text.MessageFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class PluginMessageUtil: PluginContext {
    private val bundleName = "messages"
    private var currentLocale = Locale.getDefault()
    
    private val bundleCache = ConcurrentHashMap<Locale, ResourceBundle>()
    private val formatCache = ConcurrentHashMap<String, MessageFormat>()

    fun translate(@PropertyKey(resourceBundle = "messages.messages") key: String, vararg args: Any): Component {
        var text = translate(key)
        if (!args.isEmpty()) {
            val format = formatCache.computeIfAbsent(text) { MessageFormat(it) }
            text = format.format(args)
        }
        return MiniMessage.miniMessage().deserialize(text)
    }

    private fun translate(key: String,locale: Locale = currentLocale): String {
        val bundle = bundleCache.computeIfAbsent(locale) { loadBundle(it) }
        return try { bundle.getString(key) } catch (_: MissingResourceException) { key }
    }

    private fun loadBundle(locale: Locale): ResourceBundle {
        val folder = File(plugin.dataFolder, bundleName)
        val fileName = "${bundleName}_${locale.language}_${locale.country}.properties"
        val file = File(folder, fileName)

        return if (file.exists()) {
            file.inputStream().reader(Charsets.UTF_8).use { PropertyResourceBundle(it) }
        } else {
            ResourceBundle.getBundle("$bundleName.$bundleName", locale, object : ResourceBundle.Control() {
                override fun getFallbackLocale(baseName: String?, locale: Locale?) = null
            })
        }
    }

    fun updateLocale(langTag: String) {
        currentLocale = Locale.forLanguageTag(langTag.replace("[_.]".toRegex(), "-"))
        bundleCache.clear()
        formatCache.clear()
    }
}