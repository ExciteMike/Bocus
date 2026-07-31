package com.excitemike.bocus.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.MessageList
import com.excitemike.bocus.ui.component.BocusSwipeToDismissBox
import com.excitemike.bocus.ui.component.GridWithAddButton

@Composable
fun MessageListScreen(
    modifier: Modifier = Modifier,
    messageLists: List<MessageList>,
    addMessageList: () -> Unit,
    deleteMessageList: (Int) -> Unit,
) {
    GridWithAddButton(
        data = messageLists,
        dataKey = { it.id!! },
        addButtonLabel = stringResource(R.string.add_message_list),
        onAdd = addMessageList,
        modifier = modifier.fillMaxSize(),
        messageIfEmpty = stringResource(R.string.no_alarms)
    ) { messageList ->
        val confirmFormat = stringResource(R.string.confirm_delete_message_list)
        val confirmPrompt = String.format(confirmFormat, messageList.name)

        BocusSwipeToDismissBox(
            dismissConfirmPrompt = confirmPrompt,
            onConfirm = { deleteMessageList(messageList.id!!) },
            modifier = Modifier.padding(end = 8.dp),
        ) { }
    }
}