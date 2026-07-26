package com.excitemike.bocus.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.excitemike.bocus.R
import com.excitemike.bocus.util.checkFlags
import kotlinx.coroutines.flow.Flow

const val DEFAULT_ALARM_LENGTH:Int = 30

/// Entry for alarms
@Entity(tableName = "alarms")
data class Alarm(
    /** uniquely identify each alarm */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,

    /** how to label the alarm in the ui */
    @ColumnInfo(name = "name")
    val name: String,

    /** shortest time between triggers of the alarm, in minutes */
    @ColumnInfo(name = "frequency_min")
    val frequencyMin: Int = AlarmDefaults.DEFAULT_FREQUENCY_MIN,

    /** longest time between triggers of the alarm, in minutes */
    @ColumnInfo(name = "frequency_max")
    val frequencyMax: Int = AlarmDefaults.DEFAULT_FREQUENCY_MAX,

    /** At what time of day the alarms begin. Hour part. 0-23 */
    @ColumnInfo(name = "start_hour")
    val startHour: Int = AlarmDefaults.DEFAULT_START_HOUR,
    /** At what time of day the alarms begin. Minute part. 0-59 */
    @ColumnInfo(name = "start_minute")
    val startMinute: Int = AlarmDefaults.DEFAULT_START_MINUTE,

    /** At what time of day the alarms end. Hour part. 0-23 */
    @ColumnInfo(name = "end_hour")
    val endHour: Int = AlarmDefaults.DEFAULT_END_HOUR,
    /** At what time of day the alarms end. Minute part. 0-59 */
    @ColumnInfo(name = "end_minute")
    val endMinute: Int = AlarmDefaults.DEFAULT_END_MINUTE,

    /** what to do when the alarm triggers */
    @ColumnInfo(name = "notif_mode")
    val notifMode: Int = AlarmNotifMode.DEFAULT,

    /** what to say in the notification */
    @ColumnInfo(name = "message")
    val message: String = "",

    /** how long it sounds/buzzes for. Seconds */
    @ColumnInfo(name = "alarm_length")
    val alarmLength: Int = DEFAULT_ALARM_LENGTH,

    /** repeat on which days of the week, (bitflags) */
    @ColumnInfo(name = "active_days")
    val activeDays: Int = AlarmDayOfWeekFlags.DEFAULT_ACTIVE_DAYS,

    /** when was the alarm last fired as measured by System.currentTimeMillis(), or zero */
    @ColumnInfo(name = "last_triggered_at")
    val lastTriggeredAt: Int = 0
)

/** a little glue to help Room generate a delete by id function */
data class AlarmId(val id: Int)

/** Bit flags for use with Alarm.ActiveDays */
object AlarmDayOfWeekFlags {
    const val SUNDAY:Int = 0x1
    const val MONDAY:Int = 0x2
    const val TUESDAY:Int = 0x4
    const val WEDNESDAY:Int = 0x8
    const val THURSDAY:Int = 0x10
    const val FRIDAY:Int = 0x20
    const val SATURDAY:Int = 0x40

    const val ALL_DAYS:Int = 0x7F
    const val DEFAULT_ACTIVE_DAYS:Int = 0x3E
}

/** default values for alarms */
object AlarmDefaults {
    const val DEFAULT_END_HOUR = 17
    const val DEFAULT_END_MINUTE = 0
    const val DEFAULT_FREQUENCY_MAX:Int = 25
    const val DEFAULT_FREQUENCY_MIN:Int = 15
    const val DEFAULT_START_HOUR = 10
    const val DEFAULT_START_MINUTE = 0
}

/** limiting values associated with alarms */
object AlarmLimits {
    const val NAME_LEN_MAX = 255
    const val MESSAGE_LEN_MAX = 255
}

/** possibilities for alarm notification mode */
object AlarmNotifMode {
    const val BELL = 0x1
    const val VIBRATE = 0x2
    const val BELL_AND_VIBRATE = 0x3
    const val DEFAULT = BELL_AND_VIBRATE
}

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm:Alarm): Long
    @Update
    suspend fun update(alarm: Alarm)
    @Delete(entity = Alarm::class)
    suspend fun delete(alarmId: AlarmId)
    @Query("SELECT * from alarms WHERE id = :id")
    fun getAlarm(id: Int): Flow<Alarm>
    @Query("SELECT * from alarms")
    fun getAllAlarms(): Flow<List<Alarm>>

    @Query("SELECT * from alarms")
    fun getAllAlarmsRaw(): List<Alarm>
}

/**
 * Convert the alarms active days to a short string you can display to indicate them.
 *
 * So for example if it goes every day you get "SuMoTuWeThFrSa"
 */
@Composable
fun activeDaysString(alarm: Alarm): String {
    val days = alarm.activeDays
    when (days) {
        0, AlarmDayOfWeekFlags.ALL_DAYS -> return stringResource(R.string.day_short_all)
    }
    val builder = StringBuilder(stringResource(R.string.on))
        .append(" ")
    for ((flags, stringId) in listOf(
        AlarmDayOfWeekFlags.SUNDAY to R.string.day_short_sunday,
        AlarmDayOfWeekFlags.MONDAY to R.string.day_short_monday,
        AlarmDayOfWeekFlags.TUESDAY to R.string.day_short_tuesday,
        AlarmDayOfWeekFlags.WEDNESDAY to R.string.day_short_wednesday,
        AlarmDayOfWeekFlags.THURSDAY to R.string.day_short_thursday,
        AlarmDayOfWeekFlags.FRIDAY to R.string.day_short_friday,
        AlarmDayOfWeekFlags.SATURDAY to R.string.day_short_saturday,
    )) {
        if (checkFlags(days, flags)) {
            builder.append(stringResource(stringId))
        }
    }
    return builder.toString()
}