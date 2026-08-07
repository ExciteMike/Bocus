package com.excitemike.bocus.ui.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AppPurchasedState {
    UNKNOWN,
    UNPURCHASED,
    PURCHASED
}

/**
 * connect the compose stuff to the data
 */
class BillingViewModel(context: Context) : PurchasesUpdatedListener, ViewModel() {

    /**
     * info from the store for the purchaseable items
     */
    private var allProductDetails = mutableMapOf<String, ProductDetails>()

    /**
     * job for purchase checking
     */
    private var job: Job? = null

    /**
     * state for whether things have been purchased
     */
    private val purchasedStateMutable =
        MutableStateFlow(AppPurchasedState.UNKNOWN)

    /**
     * state for whether things have been purchased
     */
    val purchasedState: StateFlow<AppPurchasedState> = purchasedStateMutable

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .enableAutoServiceReconnection()
        .build()

    /**
     * tell Google that we processed the purchase or else they'll refund it
     */
    private suspend fun acknowledgePurchase(purchaseToken: String) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        var result = withContext(Dispatchers.IO) {
            billingClient.acknowledgePurchase(acknowledgePurchaseParams)
        }
        var delayMillis = 1_000L
        while (result.responseCode != BillingClient.BillingResponseCode.OK) {
            // retry
            delay(delayMillis)
            delayMillis *= 2
            result = withContext(Dispatchers.IO) {
                billingClient.acknowledgePurchase(acknowledgePurchaseParams)
            }
        }
    }

    /**
     * prepare the android billing stuff
     */
    fun init() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    job?.cancel()
                    job = viewModelScope.launch {
                        checkProducts()
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // nothing to do because we will be auto-restarting
            }
        })
    }

    /**
     * get the list of products from the play store
     */
    private suspend fun checkProducts() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params)
        }

        if (productDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            for (productDetails in productDetailsResult.productDetailsList ?: emptyList()) {
                allProductDetails[productDetails.productId] = productDetails
            }
        }
    }

    /**
     * start the purchasing flow
     */
    fun beginPurchaseFlow(activity: Activity, productId: String) {
        // TODO: error handling
        if (!allProductDetails.containsKey(productId)) {
            return
        }
        val productDetails = allProductDetails[productId]!!
        val offers = productDetails.oneTimePurchaseOfferDetailsList!!
        if (offers.isEmpty()) {
            return
        }
        val offer = offers.first() ?: return
        val offerToken = offer.offerToken ?: return
        val productDetailsParamList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamList)
            .build()
        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        @Suppress("ControlFlowWithEmptyBody")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            // purchase screen launched
        }
    }

    /**
     *
     * clean up
     */
    override fun onCleared() {
        job?.cancel()
        job = null
        super.onCleared()
    }

    /**
     * respond as the purchases go through
     */
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                // TODO: handle cancellation
                return
            }

            BillingClient.BillingResponseCode.OK -> {
                if (purchases == null) {
                    // TODO: error handling
                    return
                }
                processPurchases(purchases)
            }

            else -> {
                // TODO: error handling
                return
            }
        }
    }

    /**
     * respond to incoming purchase info
     */
    private fun processPurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            // technically I should be doing some extra work here to verify. see https://developer.android.com/google/play/billing/security#verify
            for (product in purchase.products) {
                if ((product == PRODUCT_ID) && (purchase.purchaseState == Purchase.PurchaseState.PURCHASED)) {
                    // notify+thank the user
                    // store in the DB or datastore that it was purchased
                    if (!purchase.isAcknowledged) {
                        viewModelScope.launch { acknowledgePurchase(purchase.purchaseToken) }
                    }
                    purchasedStateMutable.value = AppPurchasedState.PURCHASED
                    return
                }
            }
        }
        purchasedStateMutable.value = AppPurchasedState.UNPURCHASED
    }

    companion object {
        const val PRODUCT_ID = "unlock"
    }
    // Google Play billing reference https://developer.android.com/google/play/billing/integrate#kts
}