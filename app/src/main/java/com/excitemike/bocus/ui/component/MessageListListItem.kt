package com.excitemike.bocus.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Message
import com.excitemike.bocus.data.MessageList
import kotlin.math.max
import kotlin.math.min

/**
 * upper bound on how much we'll show of the message preview
 */
private val MAX_MESSAGE_PREVIEW_WIDTH = 133.dp

/**
 * lower bound on how much we'll show of the message preview
 */
private val MIN_MESSAGE_PREVIEW_WIDTH = 8.dp

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
        val overflowState = rememberSaveable { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            MessageRow(
                maxRows = 2,
                heightOverFlowState = overflowState
            ) {
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
                                    minWidth = MIN_MESSAGE_PREVIEW_WIDTH,
                                    maxWidth = MAX_MESSAGE_PREVIEW_WIDTH
                                ),
                            text = message.message,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            if (overflowState.value) {
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(CardDefaults.cardColors().containerColor),
                    text = "...",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }
        }
    }
}


@Composable
private fun MessageRow(
    maxRows: Int,
    heightOverFlowState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measureables, constraints ->
        val placeables = measureables.map { it.measure(constraints) }

        data class WhatAndWhere(val placeable: Placeable, val x: Int, val y: Int)

        val layoutData = mutableListOf<WhatAndWhere>()
        var x = 0
        var y = 0
        var rowHeight = 0
        var numRows = 1
        var longestRow = 0
        for (placeable in placeables) {
            if ((x != 0) && (x + placeable.width > constraints.maxWidth)) {
                y += rowHeight
                x = 0
                numRows += 1
                if (numRows > maxRows) {
                    break
                }
            }
            if (numRows < maxRows) {
                rowHeight = max(rowHeight, placeable.height)
            }
            layoutData.add(WhatAndWhere(placeable, x, y))
            x += placeable.width
            longestRow = max(longestRow, min(constraints.maxWidth, x))
        }

        heightOverFlowState.value = (numRows > maxRows)

        layout(
            width = longestRow,
            height = y
        ) {
            layoutData.forEach {
                it.placeable.place(it.x, it.y)
            }
        }
    }
}