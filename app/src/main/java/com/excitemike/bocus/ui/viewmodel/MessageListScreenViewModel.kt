package com.excitemike.bocus.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Message
import com.excitemike.bocus.data.MessageDao
import com.excitemike.bocus.data.MessageList
import com.excitemike.bocus.data.MessageListDao
import com.excitemike.bocus.data.MessageListId
import com.excitemike.bocus.ui.BocusViewModel.Companion.MAX_MESSAGES_PER_LIST
import com.excitemike.bocus.ui.BocusViewModel.Companion.MAX_MESSAGE_LISTS
import com.excitemike.bocus.ui.BocusViewModel.Companion.TIMEOUT_MILLIS
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
     * asynchronously insert a new message
     */
    fun addMessage(
        messageListId: Long,
        defaultMessage: String,
        onError: (Int) -> Unit
    ) {
        viewModelScope.launch {
            val count = messageListDao.countMessagesInList(messageListId)
            if (count < MAX_MESSAGES_PER_LIST) {
                val message = Message(messageListId = messageListId, message = defaultMessage)
                messageDao.insert(message)
            } else {
                onError(R.string.messages_per_list_limit)
            }
        }
    }

    /**
     * asynchronously insert a new message list
     */
    fun addMessageList(
        name: String,
        onError: (Int) -> Unit
    ) {
        if (allMessageListsState.value.size < MAX_MESSAGE_LISTS) {
            val messageList = MessageList(name = name)
            viewModelScope.launch {
                messageListDao.insert(messageList)
            }
        } else {
            onError(R.string.message_list_limit)
        }
    }

    /**
     * StateFlow for the full list of [MessageList]s
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val allMessageListsState: StateFlow<List<MessageList>> = messageListDao.getAllMessageLists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )

    /**
     * remove a message list from the DB
     */
    fun deleteMessageListById(id: Long) {
        viewModelScope.launch {
            messageListDao.delete(MessageListId(id))
        }
    }

    /** update the values in an [MessageList] */
    fun updateMessageList(messageList: MessageList) {
        viewModelScope.launch {
            messageListDao.update(messageList)
        }
    }

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
            SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
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
            SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
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