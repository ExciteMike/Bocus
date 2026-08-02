package com.excitemike.bocus.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.excitemike.bocus.data.MessageList


/**
 * Dialog for choosing a message list
 *
 * @param messageLists message lists to choose from
 * @param state [BocusDialogState] object that holds the internal state of this component
 * @param onChooseMessageList callback sending the id when the user selects a [com.excitemike.bocus.data.MessageList]
 */
@Composable
fun ChooseMessageListDialog(
    messageLists: List<MessageList>,
    modifier: Modifier = Modifier,
    state: BocusDialogState = rememberBocusDialogState(false),
    onChooseMessageList: (Int) -> Unit,
) {
    BocusDialog(state) {

    }
}