package com.coderxi.plugin.fakeplayer.manager

import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerQuitedEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object FakePlayerSelector: Listener {

    private val selectedMap by lazy { ConcurrentHashMap<UUID, FakePlayer>() }
    var Player.selected : FakePlayer?
        get() = selectedMap[uniqueId]
        set(value) {
            if(value == null) selectedMap.remove(uniqueId)
            selectedMap[uniqueId] = value!!
        }

    @EventHandler
    private fun cleanup(event: FakePlayerQuitedEvent) {
        val selectorUuids = selectedMap.filter { (_, fakePlayer) -> fakePlayer.uuid == event.fakePlayer.uuid }.keys
        selectorUuids.forEach(selectedMap::remove)
    }

}