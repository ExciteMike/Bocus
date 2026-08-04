package com.excitemike.bocus.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.MessageList
import com.excitemike.bocus.ui.component.BocusSwipeToDismissBox
import com.excitemike.bocus.ui.component.GridWithAddButton
import com.excitemike.bocus.ui.component.MessageListListItem
import com.excitemike.bocus.ui.dialog.MessageListDialog
import com.excitemike.bocus.ui.viewmodel.MessageListScreenViewModel

/**
 * Composable for the message list screen.
 * @param messageListScreenViewModel
 * @param onError If something goes wrong that we need to tell the user about, it will call this to provide a resource id for the string to display
 * @param modifier modifier for this composable
 */
@Composable
fun MessageListScreen(
    messageListScreenViewModel: MessageListScreenViewModel,
    onError: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val messageLists = messageListScreenViewModel.allMessageListsState.collectAsState().value
    val messagesByListId = messageListScreenViewModel.messagesByListId.collectAsState()
    val defaultMessageListName = stringResource(R.string.default_message_list_name)
    val defaultMessage = stringResource(R.string.default_message)

    val addMessage = { messageListId: Long ->
        messageListScreenViewModel.addMessage(messageListId, defaultMessage, onError)
    }
    val updateMessageList =
        { messageList: MessageList -> messageListScreenViewModel.updateMessageList(messageList) }
    val messageList = messageListScreenViewModel.messageListState.collectAsState().value
    val messages = messageListScreenViewModel.messagesState.collectAsState().value

    if (messageList != null) {
        MessageListDialog(
            messageList = messageList,
            messages = messages,
            addMessage = addMessage,
            updateMessageList = updateMessageList,
            close = { messageListScreenViewModel.clearMessageList() }
        )
    }


    GridWithAddButton(
        data = messageLists,
        dataKey = { it.id!! },
        addButtonLabel = stringResource(R.string.add_message_list),
        onAdd = { messageListScreenViewModel.addMessageList(defaultMessageListName, onError) },
        modifier = modifier.fillMaxSize(),
        messageIfEmpty = stringResource(R.string.no_message_lists)
    ) { messageList ->
        val messageListId = messageList.id!!
        LaunchedEffect(messageListId) {
            messageListScreenViewModel.observeMessages(messageListId)
        }
        val messages = messagesByListId.value[messageListId] ?: emptyList()
        val confirmFormat = stringResource(R.string.confirm_delete_message_list)
        val confirmPrompt = String.format(confirmFormat, messageList.name)
        val isConfirming = rememberSaveable { mutableStateOf(false) }
        BocusSwipeToDismissBox(
            dismissConfirmPrompt = confirmPrompt,
            onConfirm = { messageListScreenViewModel.deleteMessageListById(messageListId) },
            modifier = Modifier.padding(end = 8.dp)
                .height(80.dp),
            state = isConfirming,
        ) {
            MessageListListItem(
                messageList = messageList,
                messages = messages,
                onEditClick = {
                    messageListScreenViewModel.loadMessageList(messageListId)
                },
                onDeleteClick = { isConfirming.value = true },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}