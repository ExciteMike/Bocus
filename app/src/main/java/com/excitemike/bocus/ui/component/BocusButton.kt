package com.excitemike.bocus.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.util.Fx

/**
 * custom button to add our style and click fx
 */
@Composable
fun BocusButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    content: @Composable () -> Unit = {},
) {
    val shape = MaterialTheme.shapes.medium
    val context = LocalContext.current
    Surface(modifier = modifier) {
        Button(
            onClick = {
                Fx.buttonClickFx(context)
                onClick()
            },
            contentPadding = contentPadding,
            shape = shape,
        ) {
            content()
        }
    }
}