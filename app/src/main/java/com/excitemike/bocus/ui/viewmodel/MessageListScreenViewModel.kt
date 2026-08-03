package com.excitemike.bocus.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excitemike.bocus.data.Message
import com.excitemike.bocus.data.MessageDao
import com.excitemike.bocus.data.MessageList
import com.excitemike.bocus.data.MessageListDao
import com.excitemike.bocus.ui.BocusViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.plus
import kotlin.collections.set

/**
 * Glue to connect composables to DB
 */
class MessageListScreenViewModel(
    private val messageListDao: MessageListDao,
    private val messageDao: MessageDao,
) : ViewModel() {
    /**
     * identify which list these messages are for
     */
    private val selectedMessageListId = MutableStateFlow<Long?>(null)

    /**
     * StateFlow for our messages
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val messagesState: StateFlow<List<Message>> =
        selectedMessageListId.flatMapLatest { messageListId ->
            if (messageListId == null) {
                flowOf(emptyList())
            } else {
                messageDao.observeListMessages(messageListId)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(BocusViewModel.TIMEOUT_MILLIS),
            emptyList()
        )

    /**
     * StateFlow for the [MessageList] owning those messages
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val messageListState: StateFlow<MessageList?> =
        selectedMessageListId.flatMapLatest { messageListId ->
            if (messageListId == null) {
                flowOf(null)
            } else {
                messageListDao.getMessageList(messageListId)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(BocusViewModel.TIMEOUT_MILLIS),
            null
        )

    /**
     * clear out the message data
     */
    fun clearMessageList() {
        selectedMessageListId.value = null
    }

    /**
     * load a new set of messages
     */
    fun loadMessageList(messageListId: Long) {
        selectedMessageListId.value = messageListId
    }

    /**
     * jobs for tracking each message lists's messages
     */
    private val jobs = mutableMapOf<Long, Job>()

    /**
     * messages tracked by list id
     */
    private val messagesByListIdMutable =
        MutableStateFlow<Map<Long, List<Message>>>(emptyMap())

    /**
     * messages tracked by list id
     */
    val messagesByListId: StateFlow<Map<Long, List<Message>>> = messagesByListIdMutable

    /**
     * if not already doing so, start tracking messages by listid
     * so that they can be accessed with the messagesBylistId property
     */
    fun observeMessages(messageListId: Long) {
        if (jobs.containsKey(messageListId)) return

        jobs[messageListId] = viewModelScope.launch {
            messageListDao.getMessagesInList(messageListId)
                .collect { messages ->
                    messagesByListIdMutable.update { current -> current + (messageListId to messages) }
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