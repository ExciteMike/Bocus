package com.excitemike.bocus.ui.dialog

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.MessageList
import com.excitemike.bocus.ui.component.BocusTextButton
import com.excitemike.bocus.util.FxType


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
    BocusDialog(
        title = stringResource(R.string.choose_message_list),
        state
    ) {
        LazyColumn(
            modifier = modifier
                .padding(start = 8.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(
                count = messageLists.size,
                key = { messageLists[it].id!! },
            ) {
                val messageList = messageLists[it]
                BocusTextButton(
                    text = messageList.name,
                    onClick = {
                        onChooseMessageList(messageList.id!!)
                        state.isOpen = false
                    },
                    fx = FxType.NORMAL
                )
            }
        }
    }
}