package com.excitemike.bocus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R

@Composable
fun AboutScreen(
    goToScreen: (AppScreens)->Unit,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "About screen",
            textAlign = TextAlign.Center
        )
        Button(onClick = { goToScreen(AppScreens.CREDITS) }) {
            Text(stringResource(R.string.credits_button))
        }
    }
}
