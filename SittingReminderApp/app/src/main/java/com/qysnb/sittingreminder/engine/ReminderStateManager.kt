package com.qysnb.sittingreminder.engine

enum class ReminderState {
    IDLE,
    STAND_UP_PENDING,
    STAND_UP_TRIGGERED,
    SIT_BACK_PENDING
}

class ReminderStateManager {

    var currentState: ReminderState = ReminderState.IDLE
        private set

    fun reset() {
        currentState = ReminderState.IDLE
    }

    fun startCycle() {
        currentState = ReminderState.STAND_UP_PENDING
    }

    fun onStandUpTriggered() {
        currentState = ReminderState.STAND_UP_TRIGGERED
    }

    fun onSitBackScheduled() {
        currentState = ReminderState.SIT_BACK_PENDING
    }

    fun onCycleComplete() {
        currentState = ReminderState.STAND_UP_PENDING
    }

    fun onStop() {
        currentState = ReminderState.IDLE
    }
}
