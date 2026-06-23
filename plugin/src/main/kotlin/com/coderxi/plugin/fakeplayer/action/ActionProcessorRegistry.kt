package com.coderxi.plugin.fakeplayer.action

import com.coderxi.plugin.fakeplayer.action.processor.ActionProcessor
import com.coderxi.plugin.fakeplayer.action.processor.AttackProcessor
import com.coderxi.plugin.fakeplayer.action.processor.JumpProcessor
import com.coderxi.plugin.fakeplayer.action.processor.MineProcessor
import com.coderxi.plugin.fakeplayer.action.processor.SneakProcessor
import com.coderxi.plugin.fakeplayer.action.processor.UseItemProcessor
import com.coderxi.plugin.fakeplayer.api.action.Action

object ActionProcessorRegistry {

    private val processors = listOf(
        AttackProcessor,
        MineProcessor,
        UseItemProcessor,
        JumpProcessor,
        SneakProcessor
    )

    private val registry = processors.associateBy { it.supportedType }

    @Suppress("UNCHECKED_CAST")
    fun <T : Action> get(action: T): ActionProcessor<T>? {
        return registry[action.type] as? ActionProcessor<T>
    }

}