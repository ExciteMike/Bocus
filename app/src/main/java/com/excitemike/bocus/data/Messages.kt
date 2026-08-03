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
 * message list entity
 */
@Entity(tableName = "message_lists")
data class MessageList(
    /** uniquely identify each message lisst */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    /** label for this list */
    @ColumnInfo(name = "name")
    val name: String,
)

/**
 * message entity
 */
@Entity(
    tableName = "messages",
    foreignKeys = [ForeignKey(
        entity = MessageList::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("message_list_id"),
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

    @ColumnInfo(name = "message_list_id", index = true)
    val messageListId: Long,

    /** message to display */
    @ColumnInfo(name = "message")
    val message: String,
)

/** a little glue to help Room generate a delete by id function */
data class MessageListId(val id: Long)

/**
 * Data access interface for message lists
 */
@Dao
interface MessageListDao {

    @Query("SELECT COUNT(*) from messages WHERE message_list_id = :id")
    suspend fun countMessagesInList(id: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(messageList: MessageList): Long

    @Update
    suspend fun update(messageList: MessageList)

    @Delete(entity = MessageList::class)
    suspend fun delete(id: MessageListId)

    @Query("SELECT * from message_lists")
    fun getAllMessageLists(): Flow<List<MessageList>>

    @Query("SELECT * from message_lists WHERE id = :id")
    fun getMessageList(id: Long): Flow<MessageList?>

    @Query("SELECT * from messages WHERE message_list_id = :id")
    fun getMessagesInList(id: Long): Flow<List<Message>>

    @Query("SELECT * from messages WHERE message_list_id = :id")
    fun getMessagesInListRaw(id: Long): List<Message>
}

/**
 * Data access interface for messages
 */
@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: Message): Long

    @Update
    suspend fun update(message: Message)

    @Delete(entity = Message::class)
    suspend fun delete(message: Message)

    @Query("SELECT * from messages WHERE id = :id")
    fun getMessage(id: Long): Message?

    /**
     * Data flow for the messages associated with a particular list
     */
    @Query("SELECT * from messages WHERE message_list_id = :messageListId")
    fun observeListMessages(messageListId: Long): Flow<List<Message>>
}