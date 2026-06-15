package com.coderxi.plugin.fakeplayer.utils

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class EventEmitter<T: Any> {

    private data class Hook(val action: (Any) -> Unit, val priority: Int)

    private val hooks = ConcurrentHashMap<String, CopyOnWriteArrayList<Hook>>()

    fun <E : T> registerEvent(eventName: String, priority: Int = 0, action: (E) -> Unit) {
        val list = hooks.computeIfAbsent(eventName) { CopyOnWriteArrayList() }
        synchronized(list) {
            val newHook = Hook({ event -> @Suppress("UNCHECKED_CAST") action(event as E) }, priority)
            val index = list.indexOfFirst { it.priority < priority }
            if (index == -1) list.add(newHook) else list.add(index, newHook)
        }
    }

    fun emit(eventName: String) {
        hooks[eventName]?.forEach { it.action(Unit) }
    }

    inline fun <reified E : T> registerEvent(priority: Int = 0, noinline action: (E) -> Unit) {
        registerEvent(E::class.simpleName!!,priority,action)
    }

    fun <E : T> emit(event: E) {
        val eventName = event::class.simpleName ?: return
        hooks[eventName]?.forEach { it.action(event) }
    }

    fun clear() = hooks.clear()

}