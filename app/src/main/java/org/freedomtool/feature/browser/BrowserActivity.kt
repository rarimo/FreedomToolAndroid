package org.freedomtool.feature.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.databinding.ActivityBrowserBinding

class BrowserActivity: BaseActivity() {

    private lateinit var binding: ActivityBrowserBinding
    private val pdfViewerURL = "https://drive.google.com/viewerng/viewer?embedded=true&url="

    val isLoading = MutableLiveData(true)
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_browser)
        binding.lifecycleOwner = this

        initButtons()
        initWebView()
        val urlAddress = intent?.getStringExtra(URL_TRANSFER_KEY)
          ?: throw IllegalStateException("No url to display found")
        binding.webView.loadUrl(
            urlAddress
        )
    }


    override fun onResume() {
        binding.webView.onResume()
        super.onResume()
    }

    override fun onPause() {
        binding.webView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.webView.destroy()
        super.onDestroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {

        binding.webView.also {
            it.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            it.settings.cacheMode = WebSettings.LOAD_DEFAULT
            it.settings.javaScriptEnabled = true
            it.settings.domStorageEnabled = true

            it.webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    binding.toolbarTitle.text = title
                }

            }

            it.webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    isLoading.value = true
                }


                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean {
                    if (request?.url.toString().endsWith(".pdf")) {
                        val url = pdfViewerURL + request?.url.toString()
                        view?.loadUrl(url)
                    } else {
                        view?.loadUrl(request?.url.toString())
                    }
                    return true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    isLoading.value = false
                }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            finish()
        }
    }

    private fun initButtons() {
        clickHelper.addViews(
            binding.backButton
        )

        clickHelper.setOnClickListener {
            when (it.id) {
                binding.backButton.id -> {
                    onBackPressed()
                }

            }
        }
    }

    companion object {
        const val URL_TRANSFER_KEY = "URL_TRANSFER_KEY"
    }
}