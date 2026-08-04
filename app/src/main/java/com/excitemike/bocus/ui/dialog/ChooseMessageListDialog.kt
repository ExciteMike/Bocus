package com.excitemike.bocus.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.MessageList
import com.excitemike.bocus.ui.component.BocusTextButton
import com.excitemike.bocus.ui.modifier.verticalScrollbar


/**
 * Dialog for choosing a message list
 *
 * @param messageLists message lists to choose from
 * @param modifier the Modifier for this Composable
 * @param close Called to notify the caller to stop showing the dialog
 * @param onChooseMessageList callback sending the id when the user selects a [com.excitemike.bocus.data.MessageList]
 */
@Composable
fun ChooseMessageListDialog(
    messageLists: List<MessageList>,
    modifier: Modifier = Modifier,
    close: () -> Unit,
    onChooseMessageList: (Long) -> Unit,
) {
    BocusDialog(
        title = stringResource(R.string.choose_message_list),
        close = close
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = modifier.fillMaxWidth()
                .verticalScroll(scrollState)
                .verticalScrollbar(scrollState)
                .padding(start = 8.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (messageList in messageLists) {
                BocusTextButton(
                    text = messageList.name,
                    onClick = {
                        onChooseMessageList(messageList.id!!)
                        close()
                    },
                )
            }
        }
    }
}