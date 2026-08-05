package com.excitemike.bocus.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val useHorizontalLayout = windowSizeClass.isWidthAtLeastBreakpoint(450)
    if (useHorizontalLayout) {
        Row(
            modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) { AboutScreenContent() }
    } else {
        Column(
            modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) { AboutScreenContent() }
    }
}

@Composable
private fun AboutScreenContent() {
    Image(
        imageVector = ImageVector.vectorResource(R.drawable.noai),
        contentDescription = stringResource(R.string.no_ai)
    )
    Text(
        modifier = Modifier.padding(16.dp).width(450.dp),
        text = stringResource(R.string.about_message),
        textAlign = TextAlign.Start
    )
}