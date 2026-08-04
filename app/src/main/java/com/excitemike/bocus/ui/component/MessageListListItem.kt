package com.excitemike.bocus.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Message
import com.excitemike.bocus.data.MessageList

/**
 * Displays a MessageList for the grid of MessageLists
 */
@Composable
fun MessageListListItem(
    messageList: MessageList,
    messages: List<Message>,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            MessageListListItemInner(
                messageList = messageList,
                messages = messages,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onEditClick()
                    },
            )
            Column {
                BocusIconButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(0.5f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit),
                    )

                }
                BocusIconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(0.5f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.edit)
                    )
                }
            }
        }
    }
}

/**
 * inner part of the MessageListListItem
 */
@Composable
private fun MessageListListItemInner(
    messageList: MessageList,
    messages: List<Message>,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = messageList.name,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        FlowRow {
            for (message in messages) {
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            shape = MaterialTheme.shapes.medium,
                            color = DividerDefaults.color
                        ),
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .sizeIn(
                                minWidth = 32.dp,
                                maxWidth = 160.dp
                            ),
                        text = message.message,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}