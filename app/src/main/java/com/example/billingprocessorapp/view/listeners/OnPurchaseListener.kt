
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase

interface OnPurchaseListener{
    fun OnProductDetailListener(billingResult: BillingResult?, productDetailsList: List<ProductDetails>)

    fun OnProuctPurchaseListener(billingResult: BillingResult?,purchaselist: List<Purchase>)
    fun OnProuctAcknowledgeListener(billingResult: BillingResult?)

}