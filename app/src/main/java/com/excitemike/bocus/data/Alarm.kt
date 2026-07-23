package com.excitemike.bocus.data

import androidx.compose.ui.res.stringResource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.excitemike.bocus.R
import kotlinx.coroutines.flow.Flow

/// what it does when the alarm goes off
enum class NotifMode(val value:Int) {
    Bell(0x01),
    Vibrate(0x10),
    BellAndVibrate(0x11)
}

const val MINUTES_PER_HOUR:Int = 60
const val TEN_AM:Int = 10 * MINUTES_PER_HOUR
const val FIVE_PM:Int = 17 * MINUTES_PER_HOUR
const val DEFAULT_START = TEN_AM
const val DEFAULT_END = FIVE_PM
const val DEFAULT_FREQUENCY_MIN:Int = 15
const val DEFAULT_FREQUENCY_MAX:Int = 25
val DEFAULT_NOTIF_MODE = NotifMode.BellAndVibrate
const val DEFAULT_ALARM_LENGTH:Int = 30
const val DEFAULT_ACTIVE_DAYS:Int = 0x3E

/// Bit flag for use with Alarm.ActiveDays
const val SUNDAY:Int = 0x1
const val MONDAY:Int = 0x2
const val TUESDAY:Int = 0x4
const val WEDNESDAY:Int = 0x8
const val THURSDAY:Int = 0x10
const val FRIDAY:Int = 0x20
const val SATURDAY:Int = 0x40

/// Entry for alarms
@Entity(tableName = "alarms")
data class Alarm(
    /// LazyColumns wants them to have a unique id
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    /// how to label the alarm in the ui
    var name: String,

    /// every X to Y minutes
    var frequencyMin: Int = DEFAULT_FREQUENCY_MIN,

    /// every X to Y minutes
    var frequencyMax: Int = DEFAULT_FREQUENCY_MAX,

    /** At what time of day the alarms begin. Hour part. 0-23 */
    val startHour: Int = AlarmMeta.DEFAULT_START_HOUR,
    /** At what time of day the alarms begin. Minute part. 0-59 */
    val startMinute: Int = AlarmMeta.DEFAULT_START_MINUTE,

    /** At what time of day the alarms end. Hour part. 0-23 */
    val endHour: Int = AlarmMeta.DEFAULT_END_HOUR,
    /** At what time of day the alarms end. Minute part. 0-59 */
    val endMinute: Int = AlarmMeta.DEFAULT_END_MINUTE,

    val endTime: Int = DEFAULT_END,

    /// what to do
    var notifMode: NotifMode = DEFAULT_NOTIF_MODE,

    /// message to put on the phone notification
    var message: String = "",

    /// whether the alarm repeats if not dismissed
    var requireDismiss: Boolean = false,

    /// how long it sounds/buzzes for. Seconds
    var alarmLength: Int = DEFAULT_ALARM_LENGTH,

    /// repeat on which days of the week, (bitflags)
    var activeDays: Int = DEFAULT_ACTIVE_DAYS,
)

object AlarmMeta {
    const val DEFAULT_END_HOUR = 17;
    const val DEFAULT_END_MINUTE = 0;
    const val DEFAULT_START_HOUR = 10;
    const val DEFAULT_START_MINUTE = 0;
    const val NAME_LEN_MAX = 255
    const val MESSAGE_LEN_MAX = 255
}

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(alarm:Alarm)
    @Update
    suspend fun update(alarm: Alarm)
    @Delete
    suspend fun delete(alarm: Alarm)
    @Query("SELECT * from alarms WHERE id = :id")
    fun getAlarm(id: Int): Flow<Alarm>
    @Query("SELECT * from alarms")
    fun getAllAlarms(): Flow<List<Alarm>>
}

/** convert an hour (0-23) and minute (0-59) to a time in a format like "1:23 pm" */
fun timeString(format:String, hour:Int, minute:Int): String {
    val displayHour: String = when (hour) {
        0, 12 -> "12"
        in 1..11 -> "$hour"
        else -> "${hour - 12}"
    }
    val amPm: String = when (hour) {
        in 0..12 -> "am"
        else -> "pm"
    }
    val displayMinute = minute.toString().padStart(2, '0')

    return String.format(
        format,
        displayHour,
        displayMinute,
        amPm)
}