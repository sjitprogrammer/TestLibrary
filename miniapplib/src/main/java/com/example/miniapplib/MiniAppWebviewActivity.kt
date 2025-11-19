package com.example.miniapplib

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.miniapplib.lib.PaymentLib
import com.example.miniapplib.lib.PaymentResult
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class MiniAppWebviewActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mini_app_webview)

        setupWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView = findViewById(R.id.paymentWebView)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            // อย่าไปบังคับ cache mode แปลก ๆ ถ้าไม่จำเป็น
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        webView.webViewClient = object : WebViewClient(){
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                Log.e("WV", "onReceivedError: ${error?.description}")
            }
        }
        webView.loadUrl("https://misternay.github.io/miniapp-jsbridge-demo")
        webView.addJavascriptInterface(this, "JSBridge")

    }

    private fun onPaymentResultFromJs(
        status: String,
        txnId: String?,
        message: String?
    ) {
        val result = when (status.lowercase()) {
            "success" -> PaymentResult.Success(
                transactionId = txnId.orEmpty()
            )
            "failed" -> PaymentResult.Failed(
                message = message ?: "Unknown error"
            )
            "canceled" -> PaymentResult.Canceled
            else -> PaymentResult.Failed("Unknown status: $status")
        }

        PaymentLib.dispatchResult(result)
        finish()
    }

    @Deprecated("This method has been deprecated")
    override fun onBackPressed() {
        PaymentLib.dispatchResult(PaymentResult.Canceled)
        super.onBackPressed()
    }

    @JavascriptInterface
    fun openScanner() {
        registerUiListener()
    }

    @JavascriptInterface
    fun getQrCode() {
        registerGetQrCode()
    }

    @JavascriptInterface
    fun closeWebview() {
        finish()
    }

    @JavascriptInterface
    fun paymentResutl(status: String, txnId: String?, message: String) {
        onPaymentResultFromJs(status, txnId, message)
    }

    private fun registerUiListener() {
        scannerLauncher.launch(
            ScanOptions().setPrompt("Scan Qr Code")
                .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        )

    }

    private fun registerGetQrCode() {
        getQrCodeLauncher.launch(
            ScanOptions().setPrompt("Scan Qr Code")
                .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        )

    }

    private val scannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result ->

        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            webView.loadUrl("javascript:bridge.openScannerImageCallback(true)")
            onPaymentResultFromJs(status = "success", txnId = result.contents, message = "")
//            Toast.makeText(this, "Scanned Value : " + result.contents, Toast.LENGTH_SHORT).show()
        }

    }

    private val getQrCodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result ->

        if (result.contents == null) {
            val errorCode = "MAW9999"
            val errorDescription = "Something Went Wrong"
            webView.loadUrl("javascript:bridge.getQrCodeCallbackError(\"${errorCode}\",\"${errorDescription}\")")
//            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            webView.loadUrl("javascript:bridge.getQrCodeCallback(true)")
//            onPaymentResultFromJs(status = "success", txnId = result.contents, message = "")
//            Toast.makeText(this, "Scanned Value : " + result.contents, Toast.LENGTH_SHORT).show()
        }

    }
}

// helper สำหรับ escape string ให้เป็น JS string literal
private fun String.toJsString(): String {
    return "\"" + this
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r") + "\""
}
