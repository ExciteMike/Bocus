package com.excitemike.bocus.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.excitemike.bocus.util.Fx
import com.excitemike.bocus.util.FxType

/**
 * A confirm/cancel prompt meant to be used inline rather than as a popup over everything
 */
@Composable
fun InlineConfirm(
    modifier: Modifier,
    confirmPrompt: String,
    confirmText: String,
    onConfirm: () -> Unit,
    cancelText: String,
    onCancel: () -> Unit,
    singleLine: Boolean = true
) {
    val context = LocalContext.current
    BackHandler {
        Fx.buttonClickFx(context, FxType.BACK)
        onCancel()
    }

    if (singleLine) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IcMessage(confirmPrompt)
            IcCancel(cancelText, onCancel)
            IcConfirm(confirmText, onConfirm)
        }
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IcMessage(confirmPrompt)
            Row {
                IcCancel(cancelText, onCancel)
                IcConfirm(confirmText, onConfirm)
            }
        }
    }
}

/**
 * message text for InlineConfirm
 */
@Composable
private fun IcMessage(message: String, modifier: Modifier = Modifier) {
    Text(modifier = modifier, text = message)
}

/**
 * cancel button for InlineConfirm
 */
@Composable
private fun IcCancel(
    cancelText: String,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    BocusButton(
        onClick = onCancel,
        fx = FxType.CANCEL,
        modifier = modifier,
    ) {
        Text(text = cancelText)
    }
}

/**
 * confirm button for InlineConfirm
 */
@Composable
private fun IcConfirm(
    confirmText: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    BocusButton(
        onClick = onConfirm,
        fx = FxType.CONFIRM,
        modifier = modifier,
    ) {
        Text(text = confirmText)
    }
}