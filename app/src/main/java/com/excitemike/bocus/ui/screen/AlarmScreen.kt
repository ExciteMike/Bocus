package com.excitemike.bocus.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.annotation.RememberInComposition
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.MessageList
import com.excitemike.bocus.ui.AlarmDetailDialog
import com.excitemike.bocus.ui.component.AlarmGridItem
import com.excitemike.bocus.ui.component.GridWithAddButton


/**
 * AlarmScreenUiState
 */
@Stable
class AlarmScreenUiState {
    @RememberInComposition
    constructor(initialSelectedAlarmIndex: Int? = null) {
        this.selectedAlarmBacking = mutableStateOf(initialSelectedAlarmIndex)
    }

    private var selectedAlarmBacking: MutableState<Int?>

    /**
     * index of the selected alarm, or null
     */
    var selectedAlarmIndex: Int?
        get() = selectedAlarmBacking.value
        set(value) {
            selectedAlarmBacking.value = value
        }

    /**
     * Saves and restores a [AlarmScreenUiState] for  [rememberSaveable]
     */
    @Suppress("RedundantNullableReturnType")
    object Saver : androidx.compose.runtime.saveable.Saver<AlarmScreenUiState, Any> {

        override fun SaverScope.save(value: AlarmScreenUiState): Any? {
            return listOf(
                value.selectedAlarmIndex,
            )
        }

        override fun restore(value: Any): AlarmScreenUiState? {
            val (selectedAlarmIndex) = value as List<*>
            return AlarmScreenUiState(
                initialSelectedAlarmIndex = selectedAlarmIndex as Int?,
            )
        }
    }
}

/**
 * create and remember a [AlarmScreenUiState].
 * The state is remembered using [rememberSaveable] and so will be saved and restored with the composition.
 */
@Composable
fun rememberAlarmScreenUiState(): AlarmScreenUiState =
    rememberSaveable(saver = AlarmScreenUiState.Saver) { AlarmScreenUiState() }

@Composable
fun AlarmScreen(
    alarms: List<Alarm>,
    messageLists: List<MessageList>,
    addAlarm: () -> Unit,
    updateAlarm: (Alarm) -> Unit,
    deleteAlarmById: (Long) -> Unit,
    modifier: Modifier = Modifier,
    state: AlarmScreenUiState = rememberAlarmScreenUiState(),
) {
    val selectedAlarmIndex = state.selectedAlarmIndex

    AlarmDetailDialog(
        alarms = alarms,
        messageLists = messageLists,
        selectedAlarmIndex = selectedAlarmIndex,
        // TODO: this always reschedules! Could be smarter!
        updateAlarm = updateAlarm,
        closeAlarmDetails = {
            state.selectedAlarmIndex = null
        }
    )

    GridWithAddButton(
        data = alarms,
        dataKey = { it.id!! },
        addButtonLabel = stringResource(R.string.add_alarm),
        onAdd = addAlarm,
        modifier = modifier.fillMaxSize(),
        messageIfEmpty = stringResource(R.string.no_alarms)
    ) { alarm ->
        AlarmGridItem(
            modifier = Modifier.height(104.dp),
            alarm = alarm,
            allAlarms = alarms,
            openAlarmDetails = { selectedAlarmIndex ->
                state.selectedAlarmIndex = selectedAlarmIndex
            },
            deleteAlarmById = deleteAlarmById
        )
    }
}
