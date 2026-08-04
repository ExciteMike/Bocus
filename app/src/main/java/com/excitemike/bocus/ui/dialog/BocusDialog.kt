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
        Card {
            Column(
                modifier = Modifier.padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )

                content()

                BocusButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = close,
                ) {
                    Text(text = stringResource(R.string.done))
                }
            }
        }
    }
}