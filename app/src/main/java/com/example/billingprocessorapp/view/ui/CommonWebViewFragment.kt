package com.example.billingprocessorapp.view.ui

import BaseFragment
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.navigation.fragment.findNavController
import com.example.billingprocessorapp.databinding.FragmentCommonWebViewBinding

import setOnMyClickListener

/**
 * A simple [Fragment] subclass.
 * Use the [CommonWebViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommonWebViewFragment : BaseFragment() {

    private var _binding: FragmentCommonWebViewBinding? = null
    var link: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCommonWebViewBinding.inflate(inflater, container, false)
        val view = _binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding!!.toolbar.tvTitle.text = arguments!!.getString("title").toString()
        link = arguments!!.getString("url").toString()
        loadWebview()
        onClickListeners()
    }

    private fun onClickListeners() {
        _binding!!.toolbar.ivLeftArro.setOnMyClickListener {
            findNavController().popBackStack()
        }
    }

    fun loadWebview() {
        val webSettings = _binding!!.webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = false
        webSettings.useWideViewPort = false
        _binding!!.webview.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(link)
                return true
            }


            override fun onPageFinished(view: WebView, url: String) {
                dismissProgressDialog()
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                dismissProgressDialog()
            }

        }
        _binding!!.webview.loadUrl(link)
    }


}