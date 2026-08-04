package com.excitemike.bocus.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.MessageList
import com.excitemike.bocus.ui.component.BocusButton
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
    addMessageList: () -> Unit,
    onChooseMessageList: (Long) -> Unit,
) {
    BocusDialog(
        title = stringResource(R.string.choose_message_list),
        close = close
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = modifier.fillMaxSize()
                .verticalScroll(scrollState)
                .verticalScrollbar(scrollState)
                .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (messageList in messageLists) {
                Surface(
                    onClick = {
                        onChooseMessageList(messageList.id!!)
                        close()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Box {
                        Text(
                            text = messageList.name,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
            BocusButton(
                onClick = addMessageList
            ) {
                Text(stringResource(R.string.add_message_list))
            }
        }
    }
}