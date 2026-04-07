package com.coderxi.plugin.fakeplayer.utils

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class EventBus {

    private data class Hook(val action: (Any) -> Unit, val priority: Int)

    private val hooks = ConcurrentHashMap<String, CopyOnWriteArrayList<Hook>>()

    fun registerEvent(eventName: String, priority: Int = 0, action: (Any) -> Unit) {
        val list = hooks.computeIfAbsent(eventName) { CopyOnWriteArrayList() }
        synchronized(list) {
            val newHook = Hook(action, priority)
            val index = list.indexOfFirst { it.priority < priority }
            if (index == -1) list.add(newHook) else list.add(index, newHook)
        }
    }

    fun emit(event: Any) {
        val eventName = event::class.simpleName ?: return
        hooks[eventName]?.forEach { it.action(event) }
    }

    fun emit(eventName: String) {
        hooks[eventName]?.forEach { it.action(Unit) }
    }

    fun clear() = hooks.clear()

}