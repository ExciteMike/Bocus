package com.excitemike.bocus.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.ui.modifier.fadeTopAndBottom
import com.excitemike.bocus.ui.modifier.verticalScrollbar
import com.excitemike.bocus.util.FxType

@Composable
fun <T> GridWithAddButton(
    data: List<T>,
    dataKey: (T) -> Any,
    addButtonLabel: String,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
    messageIfEmpty: String = "",
    content: @Composable LazyGridScope.(T) -> Unit
) {
    Column(modifier) {
        if (data.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = messageIfEmpty,
                    textAlign = TextAlign.Justify
                )
            }
        } else {
            Surface(
                tonalElevation = 5.dp,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .fadeTopAndBottom(16.dp)
            ) {
                val gridState = rememberLazyGridState()
                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp, end = 4.dp)
                        .verticalScrollbar(gridState),
                    state = gridState,
                    columns = GridCells.Adaptive(200.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    items(
                        data,
                        key = dataKey
                    ) {
                        this@LazyVerticalGrid.content(it)
                    }
                }
            }
        }

        BocusButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            onClick = onAdd,
            fx = FxType.CONFIRM
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = addButtonLabel,
                )
                Text(
                    text = addButtonLabel
                )
            }
        }
    }
}