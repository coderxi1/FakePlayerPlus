package com.coderxi.plugin.fakeplayer.utils

interface EventDispatcher<E : Any> {
    val emitter: EventEmitter
    fun emit(event: E) {
        emitter.emit(event)
    }

    val on: Listen get() = Listen(emitter)
    fun on(classSimpleName: String,priority: Int = 0, action: (Any)-> Unit) = emitter.registerEvent(classSimpleName, priority,action)

    class Listen(val bus: EventEmitter) {
        inline operator fun <reified E : Any> invoke(
            priority: Int = 0,
            noinline action: (E) -> Unit
        ) {
            val name = E::class.simpleName ?: return
            bus.registerEvent(name, priority) { event ->
                if (event is E) action(event)
            }
        }
    }
}