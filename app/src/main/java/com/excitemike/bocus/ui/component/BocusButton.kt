package com.excitemike.bocus.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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

    Button(
        modifier = modifier,
        onClick = {
            onClick()
        },
        contentPadding = contentPadding,
        shape = shape,
    ) {
        content()
    }
}

/**
 * custom button to add our style and click fx
 */
@Composable
fun BocusIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    val shape = MaterialTheme.shapes.medium

    IconButton(
        modifier = modifier,
        onClick = {
            onClick()
        },
        shape = shape,
    ) {
        content()
    }
}

/**
 * custom button to add our style and click fx
 */
@Suppress("unused")
@Composable
fun BocusTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.medium

    TextButton(
        modifier = modifier,
        onClick = {
            onClick()
        },
        shape = shape,
    ) {
        Text(text = text)
    }
}