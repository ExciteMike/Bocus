package com.excitemike.bocus.ui.dialog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.excitemike.bocus.R
import com.excitemike.bocus.ui.component.BocusButton
import com.excitemike.bocus.util.Fx
import com.excitemike.bocus.util.FxType

/**
 * Base Dialog for Bocus
 *
 * @param title Displayed at the top of the dialog
 * @param close Notifies the caller to stop showing the dialog
 * @param content Dialog contents
 */
@Composable
fun BocusDialog(
    title: String,
    close: () -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    BackHandler {
        Fx.buttonClickFx(context, FxType.BACK)
        close()
    }
    Dialog(
        onDismissRequest = close,
    ) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )

                content()

                BocusButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = close,
                    fx = FxType.CONFIRM
                ) {
                    Text(text = stringResource(R.string.done))
                }
            }
        }
    }
}