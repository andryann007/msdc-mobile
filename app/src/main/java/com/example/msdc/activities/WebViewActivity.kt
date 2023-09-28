package com.example.msdc.activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.msdc.databinding.ActivityWebviewBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics

class WebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.webView

        var url = intent.getStringExtra("url")
        if (url!!.isNotEmpty()) {
            loadUrl(url)
        } else {
            url = "https://www.google.com"
            loadUrl(url)
        }

        onBackPressedDispatcher.addCallback(
            this,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d(ContentValues.TAG, "Activity back pressed invoked")
                    finish()
                }
            }
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadUrl(url: String?) {
        webView.settings.javaScriptEnabled = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webView.webViewClient = WebViewClient()
        webView.loadUrl(url!!)
    }
}