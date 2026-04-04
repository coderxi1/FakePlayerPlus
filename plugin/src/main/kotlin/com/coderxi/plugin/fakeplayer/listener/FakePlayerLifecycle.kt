package com.coderxi.plugin.fakeplayer.listener

import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class FakePlayerLifecycle(val that: FakePlayer) {

    typealias Callback = (FakePlayer) -> Unit
    private val listeners = ConcurrentHashMap<Phase, CopyOnWriteArrayList<Callback>>()
    private fun emit(phase: Phase) = listeners[phase]?.forEach { it.invoke(that) }
    fun addEventListener(phase: Phase, callback: Callback) = listeners.computeIfAbsent(phase) { CopyOnWriteArrayList() }.add(callback)

    enum class Phase {
        PRE_SPAWN,
        POST_SPAWN,
        SPAWN_COMPLETE,
        POST_QUIT,
        AFTER_QUIT
    }

    fun onPreSpawn() {
        emit(Phase.PRE_SPAWN)
    }

    fun onPostSpawn() {
        emit(Phase.POST_SPAWN)
    }

    fun onSpawnComplete() {
        emit(Phase.SPAWN_COMPLETE)
    }

    fun onPostQuit() {
        emit(Phase.POST_QUIT)
    }

    fun onAfterQuit() {
        emit(Phase.AFTER_QUIT)
        listeners.clear()
    }

}