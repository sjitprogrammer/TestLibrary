package com.example.miniapplib.lib

import android.app.Activity
import android.content.Intent
import com.example.miniapplib.MiniAppWebviewActivity
import com.example.miniapplib.scanner.ScannerActivity

object PaymentLib {

    private var paymentCallback: ((PaymentResult) -> Unit)? = null

    // callback ของ QR Scan ใหม่
    private var scanCallback: ((QrScanResult) -> Unit)? = null

    fun startPayment(
        activity: Activity,
        onResult: (PaymentResult) -> Unit
    ) {
        paymentCallback = onResult

        val intent = Intent(activity, MiniAppWebviewActivity::class.java)
        activity.startActivity(intent)
    }

    internal fun dispatchResult(result: PaymentResult) {
        paymentCallback?.invoke(result)
        paymentCallback = null
    }

    fun startScanQr(
        activity: Activity,
        onResult: (QrScanResult) -> Unit
    ) {
        scanCallback = onResult
        val intent = Intent(activity, ScannerActivity::class.java)
        activity.startActivity(intent)
    }

    internal fun dispatchScanResult(result: QrScanResult) {
        scanCallback?.invoke(result)
        scanCallback = null
    }
}

sealed class PaymentResult {
    data class Success(val transactionId: String) : PaymentResult()
    data class Failed(val message: String) : PaymentResult()
    data object Canceled : PaymentResult()
}

sealed class QrScanResult {
    data class Success(val value: String) : QrScanResult()
    data object Canceled : QrScanResult()
}