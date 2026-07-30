package com.excitemike.bocus.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.util.Fx
import com.excitemike.bocus.util.FxType

/**
 * custom button to add our style and click fx
 */
@Composable
fun BocusButton(
    onClick: () -> Unit,
    fx: FxType,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    content: @Composable () -> Unit = {},
) {
    val shape = MaterialTheme.shapes.medium
    val context = LocalContext.current

    Button(
        modifier = modifier,
        onClick = {
            Fx.buttonClickFx(context, fx)
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
    fx: FxType,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    val shape = MaterialTheme.shapes.medium
    val context = LocalContext.current

    IconButton(
        modifier = modifier,
        onClick = {
            Fx.buttonClickFx(context, fx)
            onClick()
        },
        shape = shape,
    ) {
        content()
    }
}