package com.excitemike.bocus.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.util.Fx
import com.excitemike.bocus.util.FxType
import com.excitemike.bocus.R

/**
 * A container that can be swiped to delete an item. Handles confirmation.
 */
@Composable
fun BocusSwipeToDismissBox(
    dismissConfirmPrompt: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    onCancel: (() -> Unit)? = null,
    content: @Composable (RowScope.() -> Unit)
) {
    var isConfirming by rememberSaveable { mutableStateOf(false) }
    if (isConfirming) {
        InlineConfirm(
            modifier = modifier,
            confirmPrompt = dismissConfirmPrompt,
            confirmText = stringResource(R.string.confirm_button),
            onConfirm = {
                isConfirming = false
                onConfirm()
            },
            cancelText = stringResource(R.string.cancel_button),
            onCancel = {
                isConfirming = false
                if (onCancel != null) onCancel()
            },
            singleLine = false
        )
    } else {
        BocusSwipeToDismissBoxInner(
            modifier = modifier,
            onDismiss = { isConfirming = true },
            content = content
        )
    }
}

/**
 * The dismiss box part of BocusSwipeToDismissBox (confirmation handled outside of this)
 */
@Composable
private fun BocusSwipeToDismissBoxInner(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    content: @Composable (RowScope.() -> Unit)
) {
    val context = LocalContext.current
    val swipeToDismissState = rememberSwipeToDismissBoxState()
    val shape = MaterialTheme.shapes.medium
    SwipeToDismissBox(
        modifier = modifier,
        state = swipeToDismissState,
        backgroundContent = {
            val deleteColor = MaterialTheme.colorScheme.errorContainer
            val bgColor =
                if (swipeToDismissState.targetValue == SwipeToDismissBoxValue.Settled) {
                    Color.Transparent
                } else {
                    deleteColor
                }

            Row(
                Modifier
                    .padding(4.dp)
                    .background(
                        color = bgColor,
                        shape = shape,
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (swipeToDismissState.targetValue != SwipeToDismissBoxValue.EndToStart) {
                    Icon(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
                if (swipeToDismissState.targetValue != SwipeToDismissBoxValue.StartToEnd) {
                    Icon(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
            }
        },
        onDismiss = {
            Fx.buttonClickFx(context, FxType.SWISH)
            onDismiss()
        }
    ) {
        content()
    }
}