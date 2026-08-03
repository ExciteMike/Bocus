package com.excitemike.bocus.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.MessageList
import com.excitemike.bocus.ui.BocusViewModel
import com.excitemike.bocus.ui.component.BocusSwipeToDismissBox
import com.excitemike.bocus.ui.component.GridWithAddButton
import com.excitemike.bocus.ui.component.MessageListListItem
import com.excitemike.bocus.ui.dialog.MessageListDialog
import com.excitemike.bocus.ui.dialog.rememberBocusDialogState
import com.excitemike.bocus.ui.viewmodel.MessageListScreenViewModel

@Composable
fun MessageListScreen(
    viewModel: BocusViewModel, // TODO: use just a messageList and messages viewmodel
    messageListScreenViewModel: MessageListScreenViewModel,
    modifier: Modifier = Modifier,
) {
    val messageLists = viewModel.messageListState.collectAsState().value
    val defaultMessageListName = stringResource(R.string.default_message_list_name)
    val defaultMessage = stringResource(R.string.default_message)

    val dlgState = rememberBocusDialogState()
    val addMessage =
        { messageListId: Long -> viewModel.addMessage(messageListId, defaultMessage) }
    val updateMessageList =
        { messageList: MessageList -> viewModel.updateMessageList(messageList) }
    val messageList = messageListScreenViewModel.messageListState.collectAsState().value
    val messages = messageListScreenViewModel.messagesState.collectAsState().value

    if (messageList != null) {
        MessageListDialog(
            messageList = messageList,
            messages = messages,
            addMessage = addMessage,
            updateMessageList = updateMessageList,
            state = dlgState
        )
    }

    GridWithAddButton(
        data = messageLists,
        dataKey = { it.id!! },
        addButtonLabel = stringResource(R.string.add_message_list),
        onAdd = { viewModel.addMessageList(defaultMessageListName) },
        modifier = modifier.fillMaxSize(),
        messageIfEmpty = stringResource(R.string.no_message_lists)
    ) { messageList ->
        val messages = viewModel.getMessages(messageList.id!!).collectAsState().value
        val confirmFormat = stringResource(R.string.confirm_delete_message_list)
        val confirmPrompt = String.format(confirmFormat, messageList.name)

        val isConfirming = rememberSaveable { mutableStateOf(false) }
        BocusSwipeToDismissBox(
            dismissConfirmPrompt = confirmPrompt,
            onConfirm = { viewModel.deleteMessageListById(messageList.id) },
            modifier = Modifier.padding(end = 8.dp)
                .height(80.dp),
            state = isConfirming,
        ) {
            MessageListListItem(
                messageList = messageList,
                messages = messages,
                onEditClick = {
                    messageListScreenViewModel.loadScreenData(messageList.id)
                    dlgState.isOpen = true
                },
                onDeleteClick = { isConfirming.value = true },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}