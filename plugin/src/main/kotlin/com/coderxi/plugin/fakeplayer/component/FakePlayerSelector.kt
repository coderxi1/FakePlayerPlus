package com.coderxi.plugin.fakeplayer.component

import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.api.event.FakePlayerQuitedEvent
import com.coderxi.plugin.fakeplayer.utils.EMPTY_UUID
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object FakePlayerSelector: Listener {

    private val selectedMap by lazy { ConcurrentHashMap<UUID, FakePlayer>() }

    private val CommandSender.selectedKey get() = if (this is Player) uniqueId else EMPTY_UUID

    var CommandSender.selected : FakePlayer?
        get() = selectedMap[selectedKey]
        set(value) {
            if(value == null) selectedMap.remove(selectedKey)
            selectedMap[selectedKey] = value!!
        }

    @EventHandler
    private fun cleanup(event: FakePlayerQuitedEvent) {
        val selectorUuids = selectedMap.filter { (_, fakePlayer) -> fakePlayer.uuid == event.fakePlayer.uuid }.keys
        selectorUuids.forEach(selectedMap::remove)
    }

}