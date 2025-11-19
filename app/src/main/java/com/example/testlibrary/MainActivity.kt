package com.example.testlibrary

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.helloworld.HelloWorldHelper
import com.example.miniapplib.lib.PaymentLib
import com.example.miniapplib.lib.PaymentResult
import com.example.miniapplib.lib.QrScanResult
import com.example.testlibrary.ui.theme.TestLibraryTheme
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity AppA", HelloWorldHelper.greeting())
        enableEdgeToEdge()
        setContent {
            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = {
                            Text("Payment Demo")
                        }
                    )
                },
                content = { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        PaymentDemoScreen()
                    }
                }

            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestLibraryTheme {
        Greeting("Android")
    }
}

@Composable
fun PaymentDemoScreen() {
    val context = LocalContext.current
    val activity = context as? Activity

    var balance by remember { mutableStateOf(10_000L) }   // สมมุติเริ่มต้น
    var statusText by remember { mutableStateOf("Ready to pay or scan") }

    // พื้นหลังรวม
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = "ทดลอง flow การชำระเงินและสแกน QR จาก Library",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            // Card แสดง Balance
            BalanceCard(
                balance = balance,
                modifier = Modifier.fillMaxWidth()
            )

            // Card แสดงสถานะล่าสุด
            StatusCard(
                statusText = statusText,
                modifier = Modifier.fillMaxWidth()
            )

            // ปุ่ม Action สองปุ่ม
            ActionButtonsRow(
                onPaymentClick = {
                    if (activity == null) return@ActionButtonsRow

                    statusText = "Processing payment..."

                    PaymentLib.startPayment(
                        activity = activity
                    ) { result ->
                        when (result) {
                            is PaymentResult.Success -> {
                                statusText = "Payment Success: txnId=${result.transactionId}"

                                // ตัวอย่าง: หัก balance ใน UI (จริงควรถูกอัปเดตจาก API)
                                // balance -= args.amount
                            }

                            is PaymentResult.Failed -> {
                                statusText = "Payment Failed: ${result.message}"
                            }

                            PaymentResult.Canceled -> {
                                statusText = "Payment Canceled"
                            }
                        }
                    }
                },
                onScanQrClick = {
                    if (activity == null) return@ActionButtonsRow

                    statusText = "Scanning QR..."

                    PaymentLib.startScanQr(
                        activity = activity
                    ) { result ->
                        statusText = when (result) {
                            is QrScanResult.Success -> {
                                "QR Scanned: ${result.value}"
                            }

                            QrScanResult.Canceled -> {
                                "QR Scanned: Canceled"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BalanceCard(
    balance: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccountBalanceWallet,
                        contentDescription = "Balance",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Available Balance",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "%,d THB".format(balance),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                AssistChip(
                    onClick = { /* no-op */ },
                    label = {
                        Text("Demo mode")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                Text(
                    text = "Updated just now",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun StatusCard(
    statusText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Rounded.Payment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ActionButtonsRow(
    onPaymentClick: () -> Unit,
    onScanQrClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ปุ่ม Payment
        Button(
            onClick = onPaymentClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.CreditCard,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Open Payment",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "ชำระเงินผ่าน WebView",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        // ปุ่ม Scan QR
        OutlinedButton(
            onClick = onScanQrClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.QrCodeScanner,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Scan QR",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "ทดสอบ flow สแกน",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

