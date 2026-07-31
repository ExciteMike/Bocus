package com.excitemike.bocus.data

import kotlinx.coroutines.flow.Flow

class OfflineBocusRepository(
    private val alarmDao: AlarmDao,
    private val messageListDao: MessageListDao,
    private val messageDao: MessageDao,
) : BocusRepository {
    override fun getAllAlarmsStream(): Flow<List<Alarm>> = alarmDao.getAllAlarms()
    override suspend fun getAlarm(id: Int): Alarm? = alarmDao.getAlarm(id)
    override suspend fun insertAlarm(alarm: Alarm): Int = alarmDao.insert(alarm).toInt()
    override suspend fun deleteAlarm(alarmId: Int) = alarmDao.delete(AlarmId(alarmId))
    override suspend fun updateAlarm(alarm: Alarm) = alarmDao.update(alarm)
    override suspend fun getAllAlarmsRaw(): List<Alarm> = alarmDao.getAllAlarmsRaw()

    override fun getAllMessageListsStream(): Flow<List<MessageList>> =
        messageListDao.getAllMessageLists()

    /**
     * get a message from the DB (or null, if the id is no good)
     */
    override suspend fun getMessageList(id: Int): MessageList? = messageListDao.getMessageList(id)

    /**
     * get all messages in a list
     */
    override suspend fun getAllMessagesInList(messageListId: Int): List<Message> =
        messageListDao.getMessagesInList(messageListId)

    /**
     * get messages by id (or null, if id is no good)
     */
    override suspend fun getMessage(messageId: Int): Message? = messageDao.getMessage(messageId)
}
