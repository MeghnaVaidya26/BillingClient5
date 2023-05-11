package com.example.billingprocessorapp.view.ui

import BillingClientRepository
import OnPurchaseListener
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.example.billingprocessorapp.R
import com.example.billingprocessorapp.databinding.FragmentSubscriptionBinding
import com.example.billingprocessorapp.utils.Constant
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import makeInvisible
import makeVisible
import roundingTwoDigit
import setOnMyClickListener


/**
 * base plan and main package id for subscription is listed in constant file
 *
 */


class SubscriptionFragment : BottomSheetDialogFragment(), OnPurchaseListener {
    private var fromRestore: Boolean = false
    private var productDetailsForLaunch: ProductDetails? = null
    private var offerToken: String = ""
    private var _binding: FragmentSubscriptionBinding? = null
    var billingClientRepo: BillingClientRepository? = null
    var TAG = this.javaClass.simpleName
    var dialog: BottomSheetDialog? = null

    var purchasedSubToken = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        dialog = BottomSheetDialog(requireContext(), theme)
        dialog!!.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED

                dialog!!.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }
        return dialog!!


    }


    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT

        bottomSheet.layoutParams = layoutParams
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSubscriptionBinding.inflate(inflater, container, false)
        val view = _binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        billingClientRepo = BillingClientRepository(activity!!, this)

        onClickListeners()
    }

    override fun onResume() {
        super.onResume()

        billingClientRepo?.let {
            it.handlePendingTransaction()

        }
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            billingClientRepo?.let {
                it.getPurchasedSubscription()

            }
        }, 500)

    }

    fun onClickListeners() {

        _binding!!.tvPrivacyPolicy.setOnMyClickListener {
            val bundle = bundleOf(
                "url" to Constant.privacy_url,
                "title" to resources.getString(R.string.terms_of_use)
            )

            findNavController().navigate(R.id.common_fragment, bundle)

        }

        _binding!!.tvTermsOfUse.setOnMyClickListener {
            val bundle = bundleOf(
                "url" to Constant.terms_of_use_url,
                "title" to resources.getString(R.string.terms_of_use)
            )

            findNavController().navigate(R.id.common_fragment, bundle)

        }


        _binding!!.btContinueSub.setOnMyClickListener {
            productDetailsForLaunch?.let {
                offerToken = offerToken.ifEmpty {
                    productDetailsForLaunch!!.subscriptionOfferDetails!!.get(1).offerToken // Default offer token will be monthly subscription
                }
                billingClientRepo?.let {
                    it.launchPurchaseFlow(
                        productDetailsForLaunch!!, offerToken, purchasedSubToken
                    )
                }

            }
        }
        _binding!!.llYearly.setOnMyClickListener {

            _binding!!.llYearly.background = ResourcesCompat.getDrawable(
                context!!.resources, R.drawable.bg_green_round_corner, null
            )
            _binding!!.llMonth.background = ResourcesCompat.getDrawable(
                context!!.resources, R.drawable.bg_gray_round_corner, null
            )
            _binding!!.llWeekly.background = ResourcesCompat.getDrawable(
                context!!.resources, R.drawable.bg_gray_round_corner, null
            )
            offerToken = productDetailsForLaunch!!.subscriptionOfferDetails!!.get(0).offerToken
            _binding!!.cvDaysMonths.makeVisible()
            _binding!!.cvMonth.makeInvisible()
            _binding!!.tvContinue.text = resources.getString(R.string.try_and_subscribe)
        }


        _binding!!.llMonth.setOnMyClickListener {

            _binding!!.llYearly.background = ResourcesCompat.getDrawable(
                context!!.resources, R.drawable.bg_gray_round_corner, null
            )
            _binding!!.llMonth.background = ResourcesCompat.getDrawable(
                context!!.resources, R.drawable.bg_green_round_corner, null
            )
            _binding!!.llWeekly.background = ResourcesCompat.getDrawable(
                context!!.resources, R.drawable.bg_gray_round_corner, null
            )
            offerToken = productDetailsForLaunch!!.subscriptionOfferDetails!!.get(1).offerToken

            _binding!!.cvDaysMonths.makeInvisible()
            _binding!!.cvMonth.makeVisible()
            _binding!!.tvContinue.text = resources.getString(R.string.try_and_subscribe)
        }
        _binding!!.llWeekly.setOnMyClickListener {

            _binding!!.llYearly.background = ResourcesCompat.getDrawable(
                context!!.resources, R.drawable.bg_gray_round_corner, null
            )
            _binding!!.llMonth.background = ResourcesCompat.getDrawable(
                context!!.resources, R.drawable.bg_gray_round_corner, null
            )
            _binding!!.llWeekly.background = ResourcesCompat.getDrawable(
                context!!.resources, R.drawable.bg_green_round_corner, null
            )
            offerToken = productDetailsForLaunch!!.subscriptionOfferDetails!!.get(2).offerToken

            _binding!!.cvDaysMonths.makeInvisible()
            _binding!!.cvMonth.makeInvisible()
            _binding!!.tvContinue.text = resources.getString(R.string.continue_string)
        }

        _binding!!.tvRestore.setOnMyClickListener {

                billingClientRepo?.let {
                    fromRestore = true
                    it.getPurchasedSubscription()

                }

        }
    }

    /**
     * Replace baseplan and productid in constant file if we replace it in Google play console
     */
    override fun OnProductDetailListener(
        billingResult: BillingResult?,
        productDetailsList: List<ProductDetails>
    ) {
        // Process the result

        for (productDetails in productDetailsList) {

            productDetailsForLaunch = productDetails
            for (baseplan in productDetails.subscriptionOfferDetails!!) {
                try {
                    var currency = (baseplan.pricingPhases.pricingPhaseList.get(
                        0
                    ).formattedPrice).substring(0,1).toString()
                    if (baseplan.basePlanId == Constant.baseplanYearly) {

                        var price = (baseplan.pricingPhases.pricingPhaseList.get(
                            0
                        ).formattedPrice).substring(1).toString()
                        var formattedPrice =
                            price.replace(",", newValue = "".toString(), ignoreCase = true)
                        val monthly = formattedPrice.toDouble() / 12
                        val weekly = (monthly.toDouble() / 4)
                        activity!!.runOnUiThread {
                            _binding!!.tvYearPerMonth.text =
                                "$currency${monthly.toString().roundingTwoDigit(monthly)} "
                            _binding!!.tvYearPerWeek.text = "$currency${
                                weekly.toString().roundingTwoDigit(weekly)
                            } / Week"
                        }


                    } else if (baseplan.basePlanId == Constant.baseplanMonthly) {

                        var price = (baseplan.pricingPhases.pricingPhaseList.get(
                            0
                        ).formattedPrice).substring(1).toString()
                        var formattedPrice =
                            price.replace(",", newValue = "".toString(), ignoreCase = true)
                        val weekly = (formattedPrice.toDouble() / 4)
                        activity!!.runOnUiThread {
                            _binding!!.tvMonthMonth.text =
                                "$currency${
                                    formattedPrice.toString()
                                        .roundingTwoDigit(formattedPrice.toDouble())
                                } "
                            _binding!!.tvMonthWeek.text = "$currency${
                                weekly.toString().roundingTwoDigit(weekly)
                            } / Week"
                        }


                    } else {

                        var price = (baseplan.pricingPhases.pricingPhaseList.get(
                            0
                        ).formattedPrice).substring(1).toString()
                        var formattedPrice =
                            price.replace(",", newValue = "".toString(), ignoreCase = true)

                        activity!!.runOnUiThread {
                            _binding!!.tvWeekWeek.text =
                                "$currency${
                                    formattedPrice.toString()
                                        .roundingTwoDigit(formattedPrice.toDouble())
                                } "
                            _binding!!.tvWeek.text = "$currency${
                                formattedPrice.toString()
                                    .roundingTwoDigit(formattedPrice.toDouble())
                            } "
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun OnProuctPurchaseListener(
        billingResult: BillingResult?,
        purchaselist: List<Purchase>
    ) {
        if (billingResult!!.responseCode == BillingClient.BillingResponseCode.OK) {
            if (purchaselist.isNotEmpty()) {
                for ((i, purchase) in purchaselist.withIndex()) {
                    //Here you can manage each product, if you have multiple subscription
                    purchasedSubToken = purchase.purchaseToken

                    if (fromRestore) {
                        Toast.makeText(
                            activity!!,
                            resources.getString(R.string.subscription_is_restored),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
            }

        }

    }

    override fun OnProuctAcknowledgeListener(billingResult: BillingResult?) {

        activity?.let {
            activity!!.runOnUiThread {
                Toast.makeText(
                    activity,
                    activity!!.resources.getString(R.string.you_are_subscribed),
                    Toast.LENGTH_SHORT
                ).show()
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    dialog!!.dismiss()
                }, 500)
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        billingClientRepo?.let {
            if (it.billingClient!!.isReady) {
                it.billingClient!!.endConnection()
            }
        }
    }
}

