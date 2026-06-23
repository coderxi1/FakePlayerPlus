package com.coderxi.plugin.fakeplayer.api.action

sealed interface Action {
    val type: ActionType
    val track: ActionTrack
}

interface AttackAction : Action {
    override val type get() = ActionType.ATTACK
    override val track get() = ActionTrack.INTERACTION
}

interface MineAction : Action {
    override val type get() = ActionType.MINE
    override val track get() = ActionTrack.INTERACTION
    var freezeTick : Int
}

interface UseItemAction : Action {
    override val type get() = ActionType.USE_ITEM
    override val track get() = ActionTrack.INTERACTION
    var freezeTick : Int
}

interface JumpAction : Action {
    override val type get() = ActionType.JUMP
    override val track get() = ActionTrack.POSTURE
}

interface SneakAction : Action {
    override val type get() = ActionType.SNEAK
    override val track get() = ActionTrack.POSTURE
}

interface TurnAroundAction : Action {
    override val type get() = ActionType.TURN_AROUND
    override val track get() = ActionTrack.GLOBAL
}

interface LookAtEntityAction : Action {
    override val type get() = ActionType.LOOK_AT
    override val track get() = ActionTrack.GLOBAL
}