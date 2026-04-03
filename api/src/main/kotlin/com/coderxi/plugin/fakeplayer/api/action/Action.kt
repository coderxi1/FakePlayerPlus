package com.coderxi.plugin.fakeplayer.api.action

sealed class Action {
    data class Attack(
        override val setting: Setting = Setting.once()
    ) : InstantAction(setting)

    data class Mine(
        val x: Int,
        val y: Int,
        val z: Int,
        override val setting: Setting = Setting.interval(5)
    ) : InstantAction(setting)

    data class Use(
        override val setting: Setting = Setting.once()
    ) : InstantAction(setting)

    data class Jump(
        override val setting: Setting = Setting.once()
    ) : InstantAction(setting)

    data class LookAtNearestEntity(
        val radius: Double = 5.0
    ) : ContinuousAction()

    data class DropItem(
        override val setting: Setting = Setting.once()
    ) : InstantAction(setting)

    data class DropStack(
        override val setting: Setting = Setting.once()
    ) : InstantAction(setting)

    data class DropInventory(
        override val setting: Setting = Setting.once()
    ) : InstantAction(setting)
}