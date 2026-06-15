package com.coderxi.plugin.fakeplayer.utils

import com.coderxi.plugin.fakeplayer.context.PluginContext
import org.bukkit.Chunk
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType.INTEGER

object ChunkExtensions: PluginContext {

    private val chunkFakePlayerCountKey = NamespacedKey(plugin, "chunk_fakeplayer_count")

    private val Chunk.pdc get() = persistentDataContainer

    var Chunk.fakePlayerCount: Int
        get() = pdc.get(chunkFakePlayerCountKey, INTEGER) ?: 0
        set(value) = if (value>0) pdc.set(chunkFakePlayerCountKey, INTEGER, value) else pdc.remove(chunkFakePlayerCountKey)

}