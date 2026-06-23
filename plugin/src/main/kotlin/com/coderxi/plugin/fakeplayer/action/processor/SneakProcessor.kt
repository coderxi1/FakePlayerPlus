package com.coderxi.plugin.fakeplayer.action.processor

import com.coderxi.plugin.fakeplayer.api.action.ActionHandler
import com.coderxi.plugin.fakeplayer.api.action.ActionType
import com.coderxi.plugin.fakeplayer.api.action.SneakAction
import com.coderxi.plugin.fakeplayer.api.action.SneakOnce
import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer

object SneakProcessor : ActionProcessor<SneakAction> {

    override val supportedType get() = ActionType.SNEAK

    override fun process(fakePlayer: FakePlayer, action: SneakAction, handler: ActionHandler) {
        val player = fakePlayer.player
        if (player.isSneaking && action is SneakOnce) {
            handler.stop(action)
        } else {
            player.isSneaking = true
        }
    }

    override fun onStop(fakePlayer: FakePlayer, action: SneakAction) {
        fakePlayer.player.isSneaking = false
    }

}
