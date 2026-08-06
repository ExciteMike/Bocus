package com.excitemike.bocus.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.PurchasesUpdatedListener

class BillingViewModel(context: Context) : ViewModel() {
    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            TODO()
        }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .enableAutoServiceReconnection()
        .build()

    /**
     * prepare the android billing stuff
     */
    fun init() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    TODO("check for purchases")
                }
            }

            override fun onBillingServiceDisconnected() {
                // nothing to do because we will be auto-restarting
            }
        })
    }

    // Google Play billing reference https://developer.android.com/google/play/billing/integrate#kts
}