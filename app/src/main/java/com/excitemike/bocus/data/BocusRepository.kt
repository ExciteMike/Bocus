package com.excitemike.bocus.data

import kotlinx.coroutines.flow.Flow

// TODO: what's the point of this? use the daos directly
interface BocusRepository {
    fun getAllAlarmsStream(): Flow<List<Alarm>>
    suspend fun insertAlarm(alarm: Alarm): Int
    suspend fun deleteAlarm(alarmId: Int)
    suspend fun updateAlarm(alarm: Alarm)
    suspend fun getAlarm(id: Int): Alarm?
    suspend fun getAllAlarmsRaw(): List<Alarm>

    /**
     * data stream for message lists
     */
    fun getAllMessageListsStream(): Flow<List<MessageList>>

    /**
     * get a message from the DB (or null, if the id is no good)
     */
    suspend fun getMessageList(id: Int): MessageList?

    /**
     * get all messages in a list
     */
    suspend fun getAllMessagesInList(messageListId: Int): List<Message>

    /**
     * get messages by id (or null, if id is no good)
     */
    suspend fun getMessage(messageId: Int): Message?
}