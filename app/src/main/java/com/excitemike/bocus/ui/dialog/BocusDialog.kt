package com.excitemike.bocus.ui.dialog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.annotation.RememberInComposition
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
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
 * The editable state of BocusDialog
 */
@Stable
class BocusDialogState {

    @RememberInComposition
    constructor(
        initialIsOpen: Boolean = false
    ) {
        this.isOpenState = mutableStateOf(initialIsOpen)
    }

    private var isOpenState: MutableState<Boolean>

    /**
     * get/set whether the dialog is open
     */
    var isOpen: Boolean
        get() = isOpenState.value
        set(value) {
            isOpenState.value = value
        }

    /**
     * Saves and restores a [BocusDialogState] for [rememberSaveable]
     */
    @Suppress("RedundantNullableReturnType")
    object Saver : androidx.compose.runtime.saveable.Saver<BocusDialogState, Any> {

        override fun SaverScope.save(value: BocusDialogState): Any? {
            return listOf(
                value.isOpen,
            )
        }

        override fun restore(value: Any): BocusDialogState? {
            val (isOpen) = value as List<*>
            return BocusDialogState(
                initialIsOpen = isOpen as Boolean,
            )
        }
    }
}

/**
 * create and remember a BocusDialogState.
 * The state is remembered using rememberSaveable and so will be saved and restored with the composition.
 */
@Composable
fun rememberBocusDialogState(
    initialShow: Boolean = false
): BocusDialogState =
    rememberSaveable(saver = BocusDialogState.Saver) { BocusDialogState(initialShow) }

/**
 * Base Dialog for Bocus
 *
 * @param state [BocusDialogState] object that holds the internal state of the dialog
 */
@Composable
fun BocusDialog(
    title: String,
    state: BocusDialogState = rememberBocusDialogState(false),
    content: @Composable () -> Unit
) {
    if (!state.isOpen) {
        return
    }
    val context = LocalContext.current
    BackHandler {
        Fx.buttonClickFx(context, FxType.BACK)
        state.isOpen = false
    }
    Dialog(
        onDismissRequest = { state.isOpen = false },
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
                    onClick = { state.isOpen = false },
                    fx = FxType.CONFIRM
                ) {
                    Text(text = stringResource(R.string.done))
                }
            }
        }
    }
}