package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.CemaViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GivingScreen(
    viewModel: CemaViewModel
) {
    val context = LocalContext.current
    val givingType by viewModel.givingType.collectAsState()
    val givingAmount by viewModel.givingAmount.collectAsState()
    val paymentMethod by viewModel.givingPaymentMethod.collectAsState()
    val givingRecords by viewModel.givingRecordsList.collectAsState()
    val successMsg by viewModel.givingSuccessMessage.collectAsState()

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    var showPaystackDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "GIVING & PARTNERSHIP",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Honor God with Your Subscriptions & Offerings",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "“Every man according as he purposeth in his heart, so let him give; not grudgingly...” (2 Corinthians 9:7)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Giving Type Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Select Giving Category",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val categories = listOf("Tithes", "Offering", "Project", "Partnership")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = (cat == givingType),
                                onClick = { viewModel.givingType.value = cat },
                                label = { Text(cat) },
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Amount Input Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Enter Amount (₦)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = givingAmount,
                        onValueChange = { viewModel.givingAmount.value = it },
                        prefix = { Text("₦ ", fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Chips
                    val quickAmounts = listOf("1000", "5000", "10000", "25000", "50000")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(quickAmounts) { amt ->
                            SuggestionChip(
                                onClick = { viewModel.givingAmount.value = amt },
                                label = { Text("₦${String.format("%,d", amt.toInt())}") }
                            )
                        }
                    }
                }
            }
        }

        // Payment Method Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Payment Method",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilterChip(
                            selected = (paymentMethod == "Paystack"),
                            onClick = { viewModel.givingPaymentMethod.value = "Paystack" },
                            label = { Text("Paystack Checkout") },
                            leadingIcon = { Icon(Icons.Outlined.CreditCard, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = (paymentMethod == "Bank Transfer"),
                            onClick = { viewModel.givingPaymentMethod.value = "Bank Transfer" },
                            label = { Text("Bank Transfer") },
                            leadingIcon = { Icon(Icons.Outlined.AccountBalance, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (paymentMethod == "Bank Transfer") {
                        // GTBank Church Account Details
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "GTBank (Guaranty Trust Bank)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Account Name: CEMA Worship Centre",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Account Number: 0123456789",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Account Number", "0123456789")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Account Number Copied!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copy Account Number")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val amt = givingAmount.toDoubleOrNull() ?: 5000.0
                                viewModel.processGiving(amt)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("I Have Transferred ₦$givingAmount")
                        }
                    } else {
                        // Paystack Checkout Button
                        Button(
                            onClick = { showPaystackDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Outlined.CreditCard, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pay ₦$givingAmount via Paystack")
                        }
                    }
                }
            }
        }

        // Success Confirmation Message
        if (successMsg != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = HighlightGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = successMsg!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Payment History Section
        item {
            Text(
                text = "Payment History",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(givingRecords) { rec ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = rec.type,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${rec.paymentMethod} • ${rec.reference}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₦${String.format("%,.0f", rec.amount)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = rec.status,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = HighlightGreenDark
                        )
                    }
                }
            }
        }
    }

    // Paystack Checkout Dialog Simulation
    if (showPaystackDialog) {
        AlertDialog(
            onDismissRequest = { showPaystackDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Paystack Secure Checkout")
                }
            },
            text = {
                Column {
                    Text(text = "Completing transaction for CEMA ${givingType}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Total: ₦$givingAmount",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = "4111 •••• •••• 1111",
                        onValueChange = {},
                        label = { Text("Card Number") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = givingAmount.toDoubleOrNull() ?: 5000.0
                        viewModel.processGiving(amt)
                        showPaystackDialog = false
                    }
                ) {
                    Text("Authorise ₦$givingAmount")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPaystackDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
