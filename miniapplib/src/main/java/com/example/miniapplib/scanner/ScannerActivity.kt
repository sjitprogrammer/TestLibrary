package com.example.miniapplib.scanner

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.miniapplib.R
import com.example.miniapplib.lib.PaymentLib
import com.example.miniapplib.lib.QrScanResult
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class ScannerActivity :  AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        registerUiListener()
    }

    private fun registerUiListener() {
        scannerLauncher.launch(
            ScanOptions().setPrompt("Scan Qr Code")
                .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        )
    }

    private val scannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result ->

        if (result.contents == null) {
            Log.d("ScannerActivity","QrScanResult.Canceled")
            PaymentLib.dispatchScanResult(QrScanResult.Canceled)
        } else {
            Log.d("ScannerActivity","QrScanResult.Success : ${result.contents}")
            PaymentLib.dispatchScanResult(QrScanResult.Success(result.contents))
        }
        finish()
    }

    override fun onBackPressed() {
        PaymentLib.dispatchScanResult(QrScanResult.Canceled)
        super.onBackPressed()
    }
}