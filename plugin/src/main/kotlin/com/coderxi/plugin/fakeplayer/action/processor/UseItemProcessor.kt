package com.coderxi.plugin.fakeplayer.action.processor

import com.coderxi.plugin.fakeplayer.api.action.ActionHandler
import com.coderxi.plugin.fakeplayer.api.action.ActionTrigger.*
import com.coderxi.plugin.fakeplayer.api.action.ActionType
import com.coderxi.plugin.fakeplayer.api.action.UseItemAction
import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import org.bukkit.inventory.EquipmentSlot

object UseItemProcessor : ActionProcessor<UseItemAction> {

    override val supportedType get() = ActionType.USE_ITEM

    override fun process(fakePlayer: FakePlayer, action: UseItemAction, handler: ActionHandler) {
        if (fakePlayer.nms.isUsingItem) return
        if (action.freezeTick > 0) { action.freezeTick--; return }
        fakePlayer.nms.useItem(EquipmentSlot.HAND) {
            if (action is Continuous) {
                action.freezeTick = 5
            }
        }
        fakePlayer.nms.useItem(EquipmentSlot.OFF_HAND) {
            if (action is Continuous) {
                action.freezeTick = 5
            }
        }
    }

    override fun onStop(fakePlayer: FakePlayer, action: UseItemAction) {
        fakePlayer.nms.releaseUsingItem()
    }

}