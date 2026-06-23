package com.coderxi.plugin.fakeplayer.api.action

sealed interface ActionTrigger {
    interface Once : ActionTrigger
    interface Continuous : ActionTrigger
    interface Interval : ActionTrigger { val intervalTicks: Int }
}