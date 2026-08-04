package com.excitemike.bocus.data

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.ui.component.BocusButton
import com.excitemike.bocus.ui.component.BocusIconButton

@Composable
fun AlarmDetailsMessages(
    currentMessageList: MessageList?,
    openChooseMessageList: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.padding(start = 16.dp),
        text = stringResource(R.string.message_list_label),
        maxLines = 1,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    AlarmDetailsMessagesAfterLabel(currentMessageList, openChooseMessageList, modifier)
}

/**
 * message list controls, unlabelled
 */
@Composable
private fun AlarmDetailsMessagesAfterLabel(
    currentMessageList: MessageList?,
    openChooseMessageList: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (currentMessageList != null) {
        val messageListName = currentMessageList.name
        AlarmDetailsMessagesAfterLabelNonEmpty(
            messageListName = messageListName,
            openChooseMessageList = openChooseMessageList,
            modifier = modifier
        )
    } else {
        Empty(
            openChooseMessageList = openChooseMessageList,
            modifier = modifier
        )
    }
}

@Composable
private fun AlarmDetailsMessagesAfterLabelNonEmpty(
    messageListName: String,
    openChooseMessageList: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.padding(start = 4.dp),
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterStart),
            text = messageListName,
            maxLines = 1,
        )

        BocusIconButton(
            modifier = Modifier
                .height(32.dp) // TODO: make configurable
                .align(Alignment.CenterEnd),
            onClick = openChooseMessageList,
        ) {
            Icon(
                modifier = Modifier.height(24.dp), // TODO: make configurable
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit),
            )
        }
    }
}

/**
 * what to show when there is no message list
 */
@Composable
private fun Empty(
    openChooseMessageList: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BocusButton(
        onClick = openChooseMessageList,
        modifier = modifier.fillMaxSize(),
    ) {
        Text(text = stringResource(R.string.choose_message_list))
    }
}