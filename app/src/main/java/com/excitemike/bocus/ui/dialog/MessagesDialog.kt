package com.excitemike.bocus.ui.dialog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Message
import com.excitemike.bocus.ui.component.BocusIconButton
import com.excitemike.bocus.ui.component.BocusSwipeToDismissBox
import com.excitemike.bocus.ui.component.GridWithAddButton

private val ITEM_HEIGHT = 80.dp
private const val MAX_MESSAGE_LEN = 255

/**
 * Dialog for editing a MessageList
 */
@Composable
fun MessagesDialog(
    alarmId: Long,
    messages: List<Message>,
    close: () -> Unit,
    addMessage: () -> Unit,
    deleteMessageById: (Long) -> Unit,
    updateMessage: (Message) -> Unit,
    observeMessages: (Long) -> Unit,
) {
    LaunchedEffect(alarmId) {
        observeMessages(alarmId)
    }

    BocusDialog(
        title = stringResource(R.string.edit_message_list),
        close = close
    ) {
        GridWithAddButton(
            data = messages,
            dataKey = { message -> message.id!! },
            addButtonLabel = stringResource(R.string.add_message),
            onAdd = addMessage
        ) { message ->
            val messageId = message.id!!
            val confirmFormat = stringResource(R.string.confirm_delete_message)
            val confirmPrompt = String.format(confirmFormat, message.message)
            val isConfirming = rememberSaveable { mutableStateOf(false) }
            val isInlineEditing = rememberSaveable { mutableStateOf(false) }
            val openInlineEdit = { isInlineEditing.value = true }
            BocusSwipeToDismissBox(
                dismissConfirmPrompt = confirmPrompt,
                onConfirm = { deleteMessageById(messageId) },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .fillMaxWidth()
                    .height(ITEM_HEIGHT),
                state = isConfirming,
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable(
                            onClick = openInlineEdit,
                            onClickLabel = stringResource(R.string.edit)
                        )
                ) {
                    if (isInlineEditing.value) {
                        InlineEdit(
                            message = message,
                            updateMessage = updateMessage,
                            closeInlineEdit = { isInlineEditing.value = false }
                        )
                    } else {
                        MessageListItem(
                            message = message,
                            openInlineEdit = openInlineEdit,
                            openInlineConfirm = { isConfirming.value = true },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageListItem(
    message: Message,
    openInlineEdit: () -> Unit,
    openInlineConfirm: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message.message,
            modifier = Modifier.weight(1f)
        )

        Column {
            BocusIconButton(
                onClick = openInlineEdit,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit),
                )

            }
            BocusIconButton(
                onClick = openInlineConfirm,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.edit)
                )
            }
        }
    }
}

@Composable
private fun InlineEdit(
    message: Message,
    updateMessage: (Message) -> Unit,
    closeInlineEdit: () -> Unit,
) {
    BackHandler { closeInlineEdit() }
    Row(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val wasFocused = remember { mutableStateOf(false) }
        val focusRequester = remember { FocusRequester() }
        TextField(
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (wasFocused.value && !it.hasFocus) {
                        closeInlineEdit()
                    }
                    if (it.hasFocus) {
                        wasFocused.value = true
                    }
                },
            state = rememberTextFieldState(initialText = message.message),
            inputTransformation = InputTransformation
                .maxLength(MAX_MESSAGE_LEN)
                .then {
                    updateMessage(message.copy(message = this.toString()))
                },
            lineLimits = TextFieldLineLimits.SingleLine,
        )

        // request focus on the textfield
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        BocusIconButton(
            onClick = closeInlineEdit
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.done),
            )
        }
    }
}