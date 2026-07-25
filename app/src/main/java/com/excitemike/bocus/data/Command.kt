/**
 * used by the UI to communicate instructions to the view model
 */
package com.excitemike.bocus.data

sealed class Command {
    /** do nothing */
    object None:Command()

    /** delete an alarm */
    data class DeleteAlarm(val alarmId: Int):Command()
}