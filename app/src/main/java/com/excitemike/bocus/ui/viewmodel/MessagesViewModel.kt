package com.excitemike.bocus.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Message
import com.excitemike.bocus.data.MessageDao
import com.excitemike.bocus.data.MessageId
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val MAX_MESSAGES_PER_LIST = 255

/**
 * Glue to connect composables to DB
 */
class MessagesViewModel(
    private val messageDao: MessageDao,
    private val defaultMessageName: String
) : ViewModel() {

    /**
     * asynchronously insert a new message
     */
    fun addMessage(
        alarmId: Long,
        onError: (Int) -> Unit
    ) {
        viewModelScope.launch {
            val count = messageDao.countMessagesForAlarm(alarmId)
            if (count < MAX_MESSAGES_PER_LIST) {
                val message = Message(alarmId = alarmId, message = defaultMessageName)
                messageDao.insert(message)
            } else {
                onError(R.string.messages_per_alarm_limit)
            }
        }
    }

    /**
     * remove a message from the DB
     */
    fun deleteMessageById(id: Long) {
        viewModelScope.launch {
            messageDao.delete(MessageId(id))
        }
    }

    /**
     * update the values of a [Message]
     */
    fun updateMessage(message: Message) {
        viewModelScope.launch {
            messageDao.update(message)
        }
    }

    /**
     * jobs for tracking each message lists's messages
     */
    private val jobs = mutableMapOf<Long, Job>()

    /**
     * messages tracked by alarm id
     */
    private val messagesByAlarmIdMutable =
        MutableStateFlow<Map<Long, List<Message>>>(emptyMap())

    /**
     * messages tracked by alarm id
     */
    val messagesByAlarmId: StateFlow<Map<Long, List<Message>>> = messagesByAlarmIdMutable

    /**
     * if not already doing so, start tracking messages by listid
     * so that they can be accessed with the messagesBylistId property
     */
    fun observeMessages(alarmId: Long) {
        if (jobs.containsKey(alarmId)) return

        jobs[alarmId] = viewModelScope.launch {
            messageDao.getMessagesForAlarm(alarmId)
                .collect { messages ->
                    messagesByAlarmIdMutable.update { current -> current + (alarmId to messages) }
                }
        }
    }

    /**
     * clean up
     */
    override fun onCleared() {
        jobs.values.forEach { it.cancel() }
        super.onCleared()
    }
}