package com.coderxi.plugin.fakeplayer.api.action

abstract class InstantAction(
    open val setting: Setting = Setting.once()
) : Action() {
    data class Setting(
        val maximum: Int,
        var remains: Int,
        var interval: Int,
        var wait: Int = 0
    ) {
        companion object {
            fun once() = Setting(1, 1, 0)
            fun interval(ticks: Int) = Setting(-1, -1, ticks)
        }
    }
}