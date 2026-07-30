package com.excitemike.bocus.ui.modifier

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.max

private const val SCROLL_BAR_ALPHA_HIGH = 0.8f
private const val SCROLL_BAR_ALPHA_LOW = 0.2f
private const val TWEEN_DELAY = 1000
private const val TWEEN_DURATION_FAST = 100
private const val TWEEN_DURATION_SLOW = 300
private const val VERY_SMALL = 0.001f
private val THUMB_CORNER_RADIUS = 4.dp
private val THUMB_THICKNESS = 8.dp
private val THUMB_MIN_LENGTH = 20.dp
private val TRACK_CORNER_RADIUS = 4.dp

fun Modifier.verticalScrollbar(
    state: LazyGridState,
): Modifier {
    val indicatorState = state.scrollIndicatorState ?: return this
    val maxOffset = indicatorState.contentSize - indicatorState.viewportSize
    if (maxOffset <= 0) return this
    return scrollbar(
        state.isScrollInProgress,
        indicatorState.scrollOffset,
        maxOffset,
        indicatorState.viewportSize,
        Orientation.Vertical,
        false
    )
}


fun Modifier.verticalScrollbar(
    state: ScrollState,
): Modifier = scrollbar(
    state.isScrollInProgress,
    state.value,
    state.maxValue,
    state.viewportSize,
    Orientation.Vertical,
    true
)

@SuppressLint("FrequentlyChangingValue")
private fun Modifier.scrollbar(
    isScrollInProgress: Boolean,
    scrollOffset: Int,
    scrollOffsetMax: Int,
    viewportSize: Int,
    direction: Orientation,
    /** whether the whole scrollback needs to move (because it is rendered inside the scrollable area vs on top */
    move: Boolean
): Modifier = composed {
    val animSpec = tween<Float>(
        delayMillis = if (isScrollInProgress) 0 else TWEEN_DELAY,
        durationMillis = if (isScrollInProgress) TWEEN_DURATION_FAST else TWEEN_DURATION_SLOW,
        easing = LinearEasing
    )
    val targetAlpha = if (isScrollInProgress) SCROLL_BAR_ALPHA_HIGH else SCROLL_BAR_ALPHA_LOW
    val alpha by animateFloatAsState(targetAlpha, animSpec)
    val thumbColor = MaterialTheme.colorScheme.secondary
    val trackColor = MaterialTheme.colorScheme.onSecondary
    val layoutDirection = LocalLayoutDirection.current
    val viewportSize = viewportSize.toFloat()
    val scrollMax = scrollOffsetMax.toFloat()
    val contentSize = viewportSize + scrollMax
    drawWithContent {
        drawContent()

        val crossSize = if (direction == Orientation.Vertical) size.width else size.height

        val scrollRatio = scrollOffset.toFloat() / max(scrollMax, 1f)
        val thumbRatio = viewportSize / max(VERY_SMALL, contentSize)
        val thumbPos = positionHelper(
            direction = direction,
            layoutDirection = layoutDirection,
            objectRatio = thumbRatio,
            scrollRatio = scrollRatio,
            viewLength = viewportSize,
            contentLength = contentSize,
            crossSize = crossSize,
            move = move
        )
        val trackPos = positionHelper(
            direction = direction,
            layoutDirection = layoutDirection,
            objectRatio = 1f,
            scrollRatio = scrollRatio,
            viewLength = viewportSize,
            contentLength = contentSize,
            crossSize = crossSize,
            move = move
        )
        val thumbSize = sizeHelper(
            direction = direction,
            objectRatio = thumbRatio,
            viewLength = viewportSize
        )
        val trackSize = sizeHelper(
            direction = direction,
            objectRatio = 1f,
            viewLength = viewportSize
        )

        drawRoundRect(
            color = trackColor,
            topLeft = trackPos,
            size = trackSize,
            cornerRadius = CornerRadius(
                TRACK_CORNER_RADIUS.toPx(),
                TRACK_CORNER_RADIUS.toPx()
            ),
            alpha = alpha,
        )
        drawRoundRect(
            color = thumbColor,
            topLeft = thumbPos,
            size = thumbSize,
            cornerRadius = CornerRadius(
                THUMB_CORNER_RADIUS.toPx(),
                THUMB_CORNER_RADIUS.toPx()
            ),
            alpha = alpha,
        )
    }
}

private fun ContentDrawScope.positionHelper(
    direction: Orientation,
    layoutDirection: LayoutDirection,
    objectRatio: Float,
    scrollRatio: Float,
    viewLength: Float,
    contentLength: Float,
    crossSize: Float,
    move: Boolean
): Offset {
    val objLength = max(objectRatio * viewLength, THUMB_MIN_LENGTH.toPx())
    val maxOffset = if (move) {
        max(0f, contentLength - objLength)
    } else {
        max(0f, viewLength - objLength)
    }
    val offset = scrollRatio * maxOffset
    return if (direction == Orientation.Vertical) {
        Offset(
            x = if (layoutDirection == LayoutDirection.Ltr) {
                crossSize - THUMB_THICKNESS.toPx()
            } else {
                0f
            },
            y = offset
        )
    } else {
        Offset(
            x = offset,
            y = if (layoutDirection == LayoutDirection.Ltr) {
                crossSize - THUMB_THICKNESS.toPx()
            } else {
                0f
            }
        )
    }
}

private fun ContentDrawScope.sizeHelper(
    direction: Orientation,
    objectRatio: Float,
    viewLength: Float,
): Size {
    val objLength =
        max(objectRatio * viewLength, THUMB_MIN_LENGTH.toPx())
    return if (direction == Orientation.Vertical) {
        Size(THUMB_THICKNESS.toPx(), objLength)
    } else {
        Size(objLength, THUMB_THICKNESS.toPx())
    }
}