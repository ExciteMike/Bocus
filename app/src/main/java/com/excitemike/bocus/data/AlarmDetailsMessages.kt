package com.excitemike.bocus.data

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
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
import com.excitemike.bocus.ui.component.BocusIconButton
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

@Composable
fun AlarmDetailsMessages(
    messages: List<Message>,
    openMessagesDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column {
        Text(
            modifier = modifier.padding(start = 16.dp),
            text = stringResource(R.string.messages_label),
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AlarmDetailsMessagesAfterLabel(messages, openMessagesDialog, modifier)
    }
}

/**
 * message list controls, unlabelled
 */
@Composable
private fun AlarmDetailsMessagesAfterLabel(
    messages: List<Message>,
    openMessagesDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        MessagesDisplay(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 0.dp, end = 32.dp)
                .clickable(
                    onClickLabel = stringResource(R.string.edit),
                    onClick = openMessagesDialog
                ),
            messages
        )

        BocusIconButton(
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.CenterEnd),
            onClick = openMessagesDialog,
        ) {
            Icon(
                modifier = Modifier.height(24.dp),
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit),
            )
        }
    }
}

/**
 * Display a tiny version of the current messages for this alarm
 */
@Composable
private fun MessagesDisplay(
    modifier: Modifier,
    messages: List<Message>
) {
    Card(
        modifier = modifier.padding(4.dp)
    ) {
        val overflowState = rememberSaveable { mutableStateOf(false) }
        Box {
            MessageRow(
                modifier = Modifier.align(Alignment.TopStart),
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
                x = 0
                numRows += 1
                if (numRows > maxRows) {
                    break
                }
                y += rowHeight
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
            height = y + rowHeight
        ) {
            layoutData.forEach {
                it.placeable.place(it.x, it.y)
            }
        }
    }
}
