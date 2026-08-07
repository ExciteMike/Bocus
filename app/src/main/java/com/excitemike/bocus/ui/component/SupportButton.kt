package com.excitemike.bocus.ui.component

import android.app.Activity
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.ui.viewmodel.AppPurchasedState
import com.excitemike.bocus.ui.viewmodel.BillingViewModel
import kotlin.math.max

private const val PADDING = 8f
private const val ANIM_MAGNITUDE = 8f

@Composable
fun SupportButton(
    activity: Activity,
    billingViewModel: BillingViewModel,
    onError: (Int) -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "support_anim")
    val animVal by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val animMagnitude by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = ANIM_MAGNITUDE,
        animationSpec = infiniteRepeatable(
            animation = tween(
                3_000,
                easing = Easing { max(0f, 6f * it - 5f) }
            ),
            repeatMode = RepeatMode.Reverse
        )
    )
    val animOffset = (animMagnitude * animVal).dp
    val showSupportButton =
        rememberSaveable(billingViewModel.purchasedState) { billingViewModel.purchasedState.value != AppPurchasedState.PURCHASED }
    if (showSupportButton) {
        BocusButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = PADDING.dp + animOffset,
                    end = PADDING.dp - animOffset
                ),
            onClick = {
                val err = billingViewModel.beginPurchaseFlow(activity, BillingViewModel.PRODUCT_ID)
                if (err != null) {
                    onError(err)
                }
            },
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val supportText = stringResource(R.string.support_this_app)
                Icon(
                    modifier = Modifier
                        .height(32.dp),
                    imageVector = Icons.Default.Favorite,
                    contentDescription = supportText,
                )
                Spacer(modifier = Modifier.size(PADDING.dp))
                Text(text = supportText)
            }
        }
    }
}