package com.excitemike.bocus.ui.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Message
import com.excitemike.bocus.data.MessageList
import com.excitemike.bocus.ui.BocusViewModel
import com.excitemike.bocus.ui.component.GridWithAddButton

/**
 * Dialog for editing a MessageList
 */
@Composable
fun MessageListDialog(
    messageList: MessageList,
    messages: List<Message>,
    addMessage: (Long) -> Unit,
    updateMessageList: (MessageList) -> Unit,
    close: () -> Unit,
) {
    val context = LocalContext.current

    BocusDialog(
        title = stringResource(R.string.edit_message_list),
        close = close
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            state = rememberTextFieldState(messageList.name),
            inputTransformation = InputTransformation
                .maxLength(BocusViewModel.MAX_NAME_LEN)
                .then {
                    updateMessageList(messageList.copy(name = this.toString()))
                },
            label = { Text(stringResource(R.string.message_list_name_label)) },
            lineLimits = TextFieldLineLimits.SingleLine
        )

        GridWithAddButton(
            data = messages,
            dataKey = { message -> message.id!! },
            addButtonLabel = stringResource(R.string.add_message),
            onAdd = { addMessage(messageList.id!!) }
        ) { message ->
            Text(text = message.message)
        }
    }
}