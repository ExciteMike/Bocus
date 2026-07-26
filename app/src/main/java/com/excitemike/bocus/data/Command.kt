/**
 * used by the UI to communicate instructions to the view model
 */
package com.excitemike.bocus.data

sealed class Command {
    /** do nothing */
    object None : Command()

    /** call a callback */
    data class Callback(val cb: suspend () -> Unit) : Command()

    /** delete an alarm */
    data class DeleteAlarm(val alarmId: Int) : Command()
}