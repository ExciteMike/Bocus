package com.excitemike.bocus.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.excitemike.bocus.data.AppScreens

@Composable
fun BocusTabRow(
    currentScreenIndex: Int,
    modifier: Modifier = Modifier,
    onNav: (AppScreens) -> Unit
) {
    PrimaryTabRow(
        selectedTabIndex = currentScreenIndex,
        modifier = modifier.fillMaxWidth()
    ) {
        AppScreens.entries.forEachIndexed { index, screen ->
            Tab(
                selected = currentScreenIndex == index,
                onClick = {
                    onNav(screen)
                },
                text = {
                    Text(
                        text = stringResource(screen.labelId),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }

}