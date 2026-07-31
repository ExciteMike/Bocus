package com.excitemike.bocus.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.ui.BocusViewModel
import com.excitemike.bocus.ui.component.BocusSwipeToDismissBox
import com.excitemike.bocus.ui.component.GridWithAddButton

@Composable
fun MessageListScreen(
    viewModel: BocusViewModel,
    modifier: Modifier = Modifier,
) {
    val messageLists = viewModel.messageListState.collectAsState().value
    val defaultMessageListName = stringResource(R.string.default_message_list_name)

    GridWithAddButton(
        data = messageLists,
        dataKey = { it.id!! },
        addButtonLabel = stringResource(R.string.add_message_list),
        onAdd = { viewModel.addMessageList(defaultMessageListName) },
        modifier = modifier.fillMaxSize(),
        messageIfEmpty = stringResource(R.string.no_message_lists)
    ) { messageList ->
        val confirmFormat = stringResource(R.string.confirm_delete_message_list)
        val confirmPrompt = String.format(confirmFormat, messageList.name)

        BocusSwipeToDismissBox(
            dismissConfirmPrompt = confirmPrompt,
            onConfirm = { viewModel.deleteMessageListById(messageList.id!!) },
            modifier = Modifier.padding(end = 8.dp),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            ) {
                Text(text = messageList.name)
            }
        }
    }
}