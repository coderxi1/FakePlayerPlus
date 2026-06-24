package com.coderxi.plugin.fakeplayer.command

import com.coderxi.plugin.fakeplayer.api.action.*
import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import com.coderxi.plugin.fakeplayer.command.annotaion.Select
import com.coderxi.plugin.fakeplayer.component.FakePlayerDialog
import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand
import com.coderxi.plugin.fakeplayer.command.annotaion.PluginCommandPermission as Permission
import com.coderxi.plugin.fakeplayer.command.permission.Permission.*

@Command("fakeplayer","fp")
class FakePlayerActionCommand: PluginComponent {

    @Subcommand("action")
    @Permission(ACTION, BASIC)
    fun Player.actionUI(@Select fakePlayer: FakePlayer) {
        showDialog(FakePlayerDialog.actionListDialog(
            fakePlayer,
            tl("fakeplayer.action.attack") to { attackActionUI(fakePlayer) },
            tl("fakeplayer.action.mine") to { mineActionUI(fakePlayer) },
            tl("fakeplayer.action.use-item") to { useItemActionUI(fakePlayer) },
            tl("fakeplayer.action.jump") to { jumpActionUI(fakePlayer) },
            tl("fakeplayer.action.sneak") to { sneakActionUI(fakePlayer) },
            tl("fakeplayer.gui.action.stop-all") to { stopAction(fakePlayer) }
        ))
    }

    @Subcommand("action stop")
    @Permission(ACTION, BASIC)
    fun stopAction(@Select fakePlayer: FakePlayer) {
        fakePlayer.actions.stopAll()
    }

    @Subcommand("action attack")
    @Permission(ACTION_ATTACK, BASIC)
    fun Player.attackActionUI(@Select fakePlayer: FakePlayer) {
        showDialog(FakePlayerDialog.actionExecuteDialog(fakePlayer,
            onClickOnce = {
                fakePlayer.actions.dispatch(AttackOnce)
            },
            onClickInterval = { intervalTicks ->
                fakePlayer.actions.dispatch(AttackInterval(intervalTicks))
            },
            onClickStop = {
                fakePlayer.actions.stop(AttackAction.track)
            }
        ))
    }

    @Subcommand("action mine")
    @Permission(ACTION_MINE, BASIC)
    fun Player.mineActionUI(@Select fakePlayer: FakePlayer) {
        showDialog(FakePlayerDialog.actionExecuteDialog(fakePlayer,
            onClickContinuous = {
                fakePlayer.actions.dispatch(MineContinuous())
            },
            onClickStop = {
                fakePlayer.actions.stop(MineAction.track)
            }
        ))
    }

    @Subcommand("action use")
    @Permission(ACTION_USE_ITEM, BASIC)
    fun Player.useItemActionUI(@Select fakePlayer: FakePlayer) {
        showDialog(FakePlayerDialog.actionExecuteDialog(fakePlayer,
            onClickOnce = {
                fakePlayer.actions.dispatch(UseItemOnce)
            },
            onClickInterval = { intervalTicks ->
                fakePlayer.actions.dispatch(UseItemInterval(intervalTicks))
            },
            onClickContinuous = {
                fakePlayer.actions.dispatch(UseItemContinuous)
            },
            onClickStop = {
                fakePlayer.actions.stop(UseItemAction.track)
            }
        ))
    }

    @Subcommand("action jump")
    @Permission(ACTION_JUMP, BASIC)
    fun Player.jumpActionUI(@Select fakePlayer: FakePlayer) {
        showDialog(FakePlayerDialog.actionExecuteDialog(fakePlayer,
            onClickOnce = {
                fakePlayer.actions.dispatch(JumpOnce)
            },
            onClickInterval = { intervalTicks ->
                fakePlayer.actions.dispatch(JumpInterval(intervalTicks))
            },
            onClickContinuous = {
                fakePlayer.actions.dispatch(JumpContinuous)
            },
            onClickStop = {
                fakePlayer.actions.stop(JumpAction.track)
            }
        ))
    }

    @Subcommand("action sneak")
    @Permission(ACTION_SNEAK, BASIC)
    fun Player.sneakActionUI(@Select fakePlayer: FakePlayer) {
        showDialog(FakePlayerDialog.actionExecuteDialog(fakePlayer,
            onClickOnce = {
                fakePlayer.actions.dispatch(SneakOnce)
            },
            onClickContinuous = {
                fakePlayer.actions.dispatch(SneakContinuous)
            },
            onClickStop = {
                fakePlayer.actions.stop(SneakAction.track)
            }
        ))
    }

}