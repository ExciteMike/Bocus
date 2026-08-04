package com.excitemike.bocus.ui.viewmodel

import com.excitemike.bocus.data.Message

/**
 * viewmodel interface for editting a []MessageList]
 */
interface EditMessageListViewModel {
    /**
     * asynchronously insert a new message
     */
    fun addMessage(
        messageListId: Long,
        onError: (Int) -> Unit
    )

    /**
     * remove a message from the DB
     */
    fun deleteMessageById(id: Long)

    /**
     * if not already doing so, start tracking messages by listid
     * so that they can be accessed with the messagesBylistId property
     */
    fun observeMessages(messageListId: Long)

    /**
     * updtate the values of a [Message]
     */
    fun updateMessage(message: Message)
}