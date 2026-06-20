package com.coderxi.plugin.fakeplayer.component

import com.coderxi.plugin.fakeplayer.api.config.FakePlayerSettings
import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.input.DialogInput.bool as boolInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.dialog.DialogLike

import net.kyori.adventure.text.event.ClickCallback
import java.time.Duration

@Suppress("UnstableApiUsage")
object FakePlayerDialog: PluginComponent {

    fun settingsDialog(fakePlayer: FakePlayer, onSubmit: () -> Unit = {}): DialogLike {
        val settings = fakePlayer.settings
        val inputs = listOf(
            boolInput("collidable", tl("fakeplayer.gui.settings.collidable")).initial(settings.collidable).build(),
            boolInput("pickupItems", tl("fakeplayer.gui.settings.pickup-items")).initial(settings.pickupItems).build(),
            boolInput("invulnerable", tl("fakeplayer.gui.settings.invulnerable")).initial(settings.invulnerable).build(),
            boolInput("autoReplenish", tl("fakeplayer.gui.settings.auto-replenish")).initial(settings.autoReplenish).build()
        )
        val onSubmitClick = DialogAction.customClick(
            { view, _ ->
                fakePlayer.settings = FakePlayerSettings(
                    view.getBoolean("collidable") ?: settings.collidable,
                    view.getBoolean("pickupItems") ?: settings.pickupItems,
                    view.getBoolean("invulnerable") ?: settings.invulnerable,
                    view.getBoolean("autoReplenish") ?: settings.autoReplenish
                )
                onSubmit.invoke()
            },
            ClickCallback.Options.builder().uses(1).lifetime(Duration.ofHours(1)).build()
        )
        return Dialog.create { builder -> builder.empty()
                .base(DialogBase.builder(tl("fakeplayer.gui.settings.title",fakePlayer.name))
                    .canCloseWithEscape(true)
                    .inputs(inputs)
                    .build())
                .type(DialogType.confirmation(
                    ActionButton.create(tl("fakeplayer.gui.settings.submit-btn"),null, 100, onSubmitClick),
                    ActionButton.create(tl("fakeplayer.gui.settings.cancel-btn"),null, 100, null)
                ))
        }
    }
}