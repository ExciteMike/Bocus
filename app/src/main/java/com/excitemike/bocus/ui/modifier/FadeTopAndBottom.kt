package com.excitemike.bocus.ui.modifier

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Fade out the top and bottom of a composable so that you can tell it is scrollable.
 * Based on a similar effect at https://medium.com/@yuriyskul/applying-smooth-gradient-edges-for-scrollable-content-in-jetpack-compose-814a10fec8ca
 */
@Composable
fun Modifier.fadeTopAndBottom(
    all: Dp = 16.dp
): Modifier {
    return this.fadeTopAndBottom(all, all)
}

/**
 * Fade out the top and bottom of a composable so that you can tell it is scrollable.
 * Based on a similar effect at https://medium.com/@yuriyskul/applying-smooth-gradient-edges-for-scrollable-content-in-jetpack-compose-814a10fec8ca
 */
@Composable
fun Modifier.fadeTopAndBottom(
    top: Dp,
    bottom: Dp
): Modifier {
    val density = LocalDensity.current
    val topFadePx = with(density) { top.toPx() }
    val bottomFadePx = with(density) { bottom.toPx() }
    return this.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()

            if (topFadePx > 0) {
                val topBrush = Brush.verticalGradient(
                    0f to Color.Transparent,
                    1f to Color.Black,
                    startY = 0f,
                    endY = topFadePx
                )
                drawRect(
                    brush = topBrush,
                    blendMode = BlendMode.DstIn,
                    size = Size(size.width, topFadePx)
                )
            }

            if (bottomFadePx > 0) {
                val bottomBrush = Brush.verticalGradient(
                    0f to Color.Black,
                    1f to Color.Transparent,
                    startY = size.height - bottomFadePx,
                    endY = size.height
                )
                drawRect(
                    brush = bottomBrush,
                    blendMode = BlendMode.DstIn,
                    topLeft = Offset(0f, size.height - bottomFadePx),
                    size = Size(size.width, bottomFadePx)
                )
            }
        }
}