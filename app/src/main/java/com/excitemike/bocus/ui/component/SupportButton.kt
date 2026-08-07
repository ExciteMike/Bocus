package com.excitemike.bocus.ui.component

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.ui.viewmodel.AppPurchasedState
import com.excitemike.bocus.ui.viewmodel.BillingViewModel

@Composable
fun SupportButton(
    activity: Activity,
    billingViewModel: BillingViewModel,
) {
    val showSupportButton =
        rememberSaveable(billingViewModel.purchasedState) { billingViewModel.purchasedState.value != AppPurchasedState.PURCHASED }
    if (showSupportButton) {
        BocusButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = {
                billingViewModel.beginPurchaseFlow(activity, BillingViewModel.PRODUCT_ID)
            },
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val supportText = stringResource(R.string.support_this_app)
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Default.Favorite,
                    contentDescription = supportText,
                )
                Spacer(Modifier.size(8.dp))
                Text(text = supportText)
            }
        }
    }
}