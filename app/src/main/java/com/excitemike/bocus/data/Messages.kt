package com.excitemike.bocus.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * message entity
 */
@Entity(
    tableName = "messages",
    foreignKeys = [ForeignKey(
        entity = Alarm::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("alarm_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Message(
    /**
     * used to keep messages in a consistent order
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "alarm_id", index = true)
    val alarmId: Long,

    /** message to display */
    @ColumnInfo(name = "message")
    val message: String,
)

/** a little glue to help Room generate a delete by id function */
data class MessageId(val id: Long)

/**
 * Data access interface for messages
 */
@Dao
interface MessageDao {

    @Query("SELECT COUNT(*) from messages WHERE alarm_id = :alarmId")
    suspend fun countMessagesForAlarm(alarmId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: Message): Long

    @Update
    suspend fun update(message: Message)

    @Delete(entity = Message::class)
    suspend fun delete(messageId: MessageId)

    @Query("SELECT * from messages WHERE id = :id")
    fun getMessage(id: Long): Message?

    @Query("SELECT * from messages WHERE alarm_id = :id")
    fun getMessagesForAlarm(id: Long): Flow<List<Message>>

    @Query("SELECT * from messages WHERE alarm_id = :id")
    suspend fun getMessagesForAlarmRaw(id: Long): List<Message>

    /**
     * Data flow for the messages associated with a particular list
     */
    @Query("SELECT * from messages WHERE alarm_id = :alarmId")
    fun observeMessagesForAlarm(alarmId: Long): Flow<List<Message>>
}