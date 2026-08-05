package com.excitemike.bocus.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Message
import com.excitemike.bocus.data.MessageDao
import com.excitemike.bocus.data.MessageId
import com.excitemike.bocus.data.MessageList
import com.excitemike.bocus.data.MessageListDao
import com.excitemike.bocus.data.MessageListId
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

const val MAX_MESSAGE_LISTS = 255
const val MAX_MESSAGES_PER_LIST = 255

/**
 * Glue to connect composables to DB
 */
class MessageListScreenViewModel(
    private val messageListDao: MessageListDao,
    private val messageDao: MessageDao,
    private val defaultMessageName: String
) : EditMessageListViewModel, ViewModel() {

    /**
     * asynchronously insert a new message
     */
    override fun addMessage(
        messageListId: Long,
        onError: (Int) -> Unit
    ) {
        viewModelScope.launch {
            val count = messageListDao.countMessagesInList(messageListId)
            if (count < MAX_MESSAGES_PER_LIST) {
                val message = Message(messageListId = messageListId, message = defaultMessageName)
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
     * remove a message from the DB
     */
    override fun deleteMessageById(id: Long) {
        viewModelScope.launch {
            messageDao.delete(MessageId(id))
        }
    }

    /**
     * remove a message list from the DB
     */
    fun deleteMessageListById(id: Long) {
        viewModelScope.launch {
            messageListDao.delete(MessageListId(id))
        }
    }

    /**
     * update the values of a [Message]
     */
    override fun updateMessage(message: Message) {
        viewModelScope.launch {
            messageDao.update(message)
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
    override fun observeMessages(messageListId: Long) {
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