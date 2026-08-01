package com.excitemike.bocus.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.util.Fx
import com.excitemike.bocus.util.FxType
import com.excitemike.bocus.R
import kotlinx.coroutines.launch

/**
 * A container that can be swiped to delete an item. Handles confirmation.
 */
@Composable
fun BocusSwipeToDismissBox(
    dismissConfirmPrompt: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    state: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) },
    onCancel: (() -> Unit)? = null,
    content: @Composable (RowScope.() -> Unit)
) {
    val swipeToDismissState = rememberSwipeToDismissBoxState()
    val scope = rememberCoroutineScope()
    Box(modifier) {
        BocusSwipeToDismissBoxInner(
            swipeToDismissState = swipeToDismissState,
            modifier = Modifier.fillMaxSize(),
            onDismiss = { state.value = true },
            content = content
        )
        if (state.value) {
            InlineConfirm(
                modifier = Modifier.fillMaxSize(),
                confirmPrompt = dismissConfirmPrompt,
                confirmText = stringResource(R.string.confirm_button),
                onConfirm = {
                    state.value = false
                    scope.launch { swipeToDismissState.reset() }
                    onConfirm()
                },
                cancelText = stringResource(R.string.cancel_button),
                onCancel = {
                    state.value = false
                    scope.launch { swipeToDismissState.reset() }
                    if (onCancel != null) onCancel()
                },
                singleLine = false
            )
        }
    }
}

/**
 * The dismiss box part of BocusSwipeToDismissBox (confirmation handled outside of this)
 */
@Composable
private fun BocusSwipeToDismissBoxInner(
    swipeToDismissState: SwipeToDismissBoxState,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    content: @Composable (RowScope.() -> Unit)
) {
    val context = LocalContext.current
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
                Icon(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
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