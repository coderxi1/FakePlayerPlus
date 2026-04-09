package com.coderxi.plugin.fakeplayer.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt

object MinecraftSpritesUtil {

    private val gson = GsonComponentSerializer.gson()
    private fun sprite(name: String, atlas: String): String = """{"sprite":"$name","atlas":"$atlas"}"""

    private val healthHeartsSprites = ConcurrentHashMap<Int, Component>()

    init {
        val heartsFull = sprite("hud/heart/full", "minecraft:gui")
        val heartsNull = sprite("hud/heart/container", "minecraft:gui")
        (0..10).forEach { i ->
            val list = mutableListOf<String>()
            repeat(i) { list.add(heartsFull) }
            repeat(10 - i) { list.add(heartsNull) }
            val finalJson = "[${list.joinToString(",")}]"
            healthHeartsSprites[i] = gson.deserialize(finalJson)
        }
    }

    fun health2heartsSprites(player: Player): Component {
        return health2heartsSprites(player.health,player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0)
    }

    fun health2heartsSprites(health: Double, maxHealth: Double): Component {
        if (health <= 0.0) return healthHeartsSprites[0] ?: Component.empty()
        val percent = (health / maxHealth).coerceIn(0.0, 1.0)
        val rounded = (percent * 10).roundToInt()
        return healthHeartsSprites[if (health > 0.0 && rounded == 0) 1 else rounded] ?: Component.empty()
    }

}