package com.coderxi.plugin.fakeplayer.action.processor

import com.coderxi.plugin.fakeplayer.api.action.ActionHandler
import com.coderxi.plugin.fakeplayer.api.action.ActionType
import com.coderxi.plugin.fakeplayer.api.action.JumpAction
import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer

object JumpProcessor : ActionProcessor<JumpAction> {

    override val supportedType get() = ActionType.JUMP

    override fun process(fakePlayer: FakePlayer, action: JumpAction, handler: ActionHandler) {
        fakePlayer.player.isJumping = true
    }

    override fun onStop(fakePlayer: FakePlayer, action: JumpAction) {
        fakePlayer.player.isJumping = false
    }
}
