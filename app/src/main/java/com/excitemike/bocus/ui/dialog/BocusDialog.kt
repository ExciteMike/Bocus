package com.excitemike.bocus.ui.dialog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.excitemike.bocus.R
import com.excitemike.bocus.ui.component.BocusButton

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
    BackHandler {
        close()
    }
    Dialog(
        onDismissRequest = close,
    ) {
        Card(modifier = Modifier.imePadding()) {
            Column(
                modifier = Modifier.padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )

                Column(Modifier.weight(1f)) {
                    content()
                }

                BocusButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .defaultMinSize(minWidth = 0.dp),
                    onClick = close,
                ) {
                    Text(text = stringResource(R.string.done))
                }
            }
        }
    }
}