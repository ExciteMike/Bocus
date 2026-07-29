package com.excitemike.bocus.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.byValue
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R

@Composable
fun MinMax(
    currentMin: Int,
    currentMax: Int,
    label: String,
    onMinChanged: (Int) -> Unit,
    onMaxChanged: (Int) -> Unit,
) {
    val minState = rememberTextFieldState(currentMin.toString())
    val maxState = rememberTextFieldState(currentMax.toString())
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = label,
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row {
            MinMaxField(
                modifier = Modifier.weight(1f),
                textFieldState = minState,
                label = stringResource(R.string.minimum),
                onValueChanged = onMinChanged,
                onBlur = {
                    // if they set min to more than max, bump up the max
                    val minVal = minState.text.toString().toIntOrNull() ?: 1
                    val maxVal = maxState.text.toString().toIntOrNull() ?: 1
                    if (minVal > maxVal) {
                        maxState.edit { replace(0, length, minVal.toString()) }
                        onMaxChanged(minVal)
                    }
                }
            )
            Spacer(Modifier.width(8.dp))
            MinMaxField(
                modifier = Modifier.weight(1f),
                textFieldState = maxState,
                label = stringResource(R.string.maximum),
                onValueChanged = onMaxChanged,
                onBlur = {
                    // if they set max to less than min, bump down the min
                    val minVal = minState.text.toString().toIntOrNull() ?: 1
                    val maxVal = maxState.text.toString().toIntOrNull() ?: 1
                    if (maxVal < minVal) {
                        minState.edit { replace(0, length, maxVal.toString()) }
                        onMinChanged(maxVal)
                    }
                }
            )
        }
    }
}


@Composable
fun MinMaxField(
    textFieldState: TextFieldState,
    label: String,
    modifier: Modifier = Modifier,
    onValueChanged: (Int) -> Unit = {},
    onBlur: () -> Unit = {}
) {
    TextField(
        modifier = modifier
            .onFocusChanged {
                if (!it.isFocused) {
                    onBlur()
                }
            },
        state = textFieldState,
        inputTransformation = forceDigits.then {
            val newValue = this.toString().toIntOrNull() ?: 0
            onValueChanged(newValue)
        },
        label = { Text(label) },
        lineLimits = TextFieldLineLimits.SingleLine
    )
}

/**
 * force the CharSequence to be a one or two-digit non-negative integer
 */
private val forceDigits = InputTransformation
    .byValue { current, proposed -> forceNDigits(current, proposed, 2) }

/**
 * force the CharSequence to be a one or two-digit non-negative integer
 */
@Suppress("SameParameterValue")
private fun forceNDigits(
    current: CharSequence,
    proposed: CharSequence,
    maxDigits: Int
): CharSequence {
    require(maxDigits > 0)
    require(maxDigits < 99)
    if (proposed.isEmpty()) {
        return "0"
    }
    if (proposed.matches("0".toRegex())) {
        return proposed
    }
    val trimmed = proposed.replace("^0+".toRegex(), "")
    if (!"""\d{1,${maxDigits}}""".toRegex().matches(trimmed)) {
        return current
    }
    return trimmed
}