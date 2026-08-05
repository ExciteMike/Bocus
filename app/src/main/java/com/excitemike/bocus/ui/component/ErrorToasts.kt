package com.excitemike.bocus.ui.component

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TOAST_RESET_DELAY = 3_000L

@Composable
fun ErrorToasts(
    messageResId: Int?,
    onTimeout: () -> Unit
) {
    val currentTimeout by rememberUpdatedState(onTimeout)

    val toastMessage = if (messageResId == null) {
        null
    } else {
        stringResource(messageResId)
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    DisposableEffect(toastMessage) {
        if (toastMessage != null) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show()
            val job = coroutineScope.launch {
                delay(TOAST_RESET_DELAY)
                currentTimeout()
            }
            onDispose {
                job.cancel()
            }
        } else {
            onDispose { }
        }
    }
}