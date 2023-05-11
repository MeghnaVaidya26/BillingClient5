
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProrationMode
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryResponseListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.QueryPurchasesParams

class BillingClientRepository(var activity: Activity, var onPurchaseListener: OnPurchaseListener) :
    DefaultLifecycleObserver {

    var billingClient: BillingClient? = null

    init {

        billingPurchaseListener()
    }

     fun billingPurchaseListener() {
        billingClient =
            BillingClient.newBuilder(activity!!).enablePendingPurchases().setListener { p0, p1 ->
                if (p0.responseCode == BillingClient.BillingResponseCode.OK && p1 != null) {
                    for (purchase in p1) {
                        verifySubPurchase(purchase)
                    }
                }
            }.build()

        establishConnection()
    }

    fun establishConnection() {
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Log.e("=====purchase== sub", "----"+billingResult.responseCode)

                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun showProducts() {

        val productList: ArrayList<QueryProductDetailsParams.Product> = ArrayList()
        productList.add(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("")
                .setProductType(BillingClient.ProductType.SUBS).build()
        )
        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()
        billingClient!!.queryProductDetailsAsync(
            params
        ) { billingResult: BillingResult?, productDetailsList: List<ProductDetails> ->
            // Process the result
            onPurchaseListener.OnProductDetailListener(billingResult, productDetailsList)
        }


    }


    fun launchPurchaseFlow(
        productDetails: ProductDetails,
        offerToken: String,
        purchasedToken: String
    ) {
        val productDetailsParamsList: List<BillingFlowParams.ProductDetailsParams> = ArrayList(
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken).build()
            )
        )
        if (purchasedToken.isNotEmpty()) {

            val SubscriptionUpdateParams: BillingFlowParams.SubscriptionUpdateParams =
                BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                    .setOldPurchaseToken(purchasedToken)
                    .setReplaceProrationMode(ProrationMode.IMMEDIATE_WITHOUT_PRORATION).build()


            val billingFlowParams =
                BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList)
                    .setSubscriptionUpdateParams(SubscriptionUpdateParams)
                    .build()
            val billingResult = billingClient!!.launchBillingFlow(activity, billingFlowParams)


        } else {

            val billingFlowParams1 =
                BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList)
                    .build()
            val billingResult = billingClient!!.launchBillingFlow(activity, billingFlowParams1)


        }

    }

    fun verifySubPurchase(purchases: Purchase) {
        billingClient?.let {
            if (it.isReady) {
                val acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchases.purchaseToken)
                        .build()
                it.acknowledgePurchase(
                    acknowledgePurchaseParams
                ) { billingResult: BillingResult ->

                    onPurchaseListener.OnProuctAcknowledgeListener(billingResult)
                }
            }
        }

    }

    fun handlePendingTransaction() {
        if (billingClient!!.isReady) {

            billingClient!!.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS)
                    .build()
            ) { billingResult: BillingResult, list: List<Purchase> ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    for (purchase in list) {
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                            verifySubPurchase(purchase)
                        }
                    }
                }
            }
        }
    }


    fun getPurchasedSubscription() {
        Log.e("=====purchase== sub", "----")

        if (billingClient!!.isReady) {
            Log.e("=====purchaseempty=== r", "----")

            billingClient!!.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.SUBS).build()
            ) { billingResult1: BillingResult, list: List<Purchase> ->
                onPurchaseListener.OnProuctPurchaseListener(billingResult1, list)
            }
        }
    }

    fun getPurchasedAllSubscription() {
        if (billingClient!!.isReady) {
            billingClient!!.queryPurchaseHistoryAsync(QueryPurchaseHistoryParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS).build(),
                PurchaseHistoryResponseListener { billingResult, purchaseHistoryRecords ->

                    if (purchaseHistoryRecords?.isNotEmpty() == true) {
                        for ((i, purchase) in purchaseHistoryRecords.withIndex()) {
                            //Here you can manage each product, if you have multiple subscription
                            Log.d(
                                "purchaseTime",
                                purchase.purchaseTime.toString()
                            ) // Get to see the order information

                        }
                    } else {
                    }

                })
        }

    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
    }

}