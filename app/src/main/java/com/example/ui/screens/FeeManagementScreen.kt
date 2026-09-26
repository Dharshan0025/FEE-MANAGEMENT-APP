package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FeeRecordEntity
import com.example.ui.components.CollectPaymentDialog
import com.example.ui.components.CreateFeeInvoiceDialog
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.ReminderDialog
import com.example.ui.components.StatusBadge
import com.example.ui.viewmodel.NrithyalayaViewModel
import com.example.util.NrithyalayaUtils

@Composable
fun FeeManagementScreen(
    viewModel: NrithyalayaViewModel
) {
    val feeRecords by viewModel.allFeeRecords.collectAsState()
    val students by viewModel.allStudents.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }

    var selectedFeeForPayment by remember { mutableStateOf<FeeRecordEntity?>(null) }
    var selectedFeeForReceipt by remember { mutableStateOf<FeeRecordEntity?>(null) }
    var selectedFeeForReminder by remember { mutableStateOf<FeeRecordEntity?>(null) }
    var showCreateInvoiceDialog by remember { mutableStateOf(false) }

    val filteredFees = remember(feeRecords, searchQuery, selectedFilter) {
        feeRecords.filter { fee ->
            val matchesFilter = when (selectedFilter) {
                "OVERDUE" -> fee.status == "OVERDUE"
                "PENDING" -> fee.status == "PENDING"
                "PARTIAL" -> fee.status == "PARTIAL"
                "PAID" -> fee.status == "PAID"
                else -> true
            }
            val matchesQuery = searchQuery.isBlank() ||
                fee.studentName.contains(searchQuery, ignoreCase = true) ||
                fee.receiptNo.contains(searchQuery, ignoreCase = true) ||
                fee.discipline.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesQuery
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateInvoiceDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Create Invoice")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Issue Invoice")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search and Status Filters
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    },
                    placeholder = { Text("Search by student, receipt # or discipline") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val filters = listOf(
                        "ALL" to "All Records",
                        "OVERDUE" to "Overdue Dues",
                        "PENDING" to "Pending",
                        "PARTIAL" to "Partially Paid",
                        "PAID" to "Completed Receipts"
                    )
                    items(filters) { (key, label) ->
                        val isSelected = selectedFilter == key
                        Surface(
                            onClick = { selectedFilter = key },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Fee Records List
            if (filteredFees.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No fee records match the selected criteria.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredFees) { fee ->
                        FeeRecordCard(
                            fee = fee,
                            onCollectClick = { selectedFeeForPayment = fee },
                            onReceiptClick = { selectedFeeForReceipt = fee },
                            onRemindClick = { selectedFeeForReminder = fee }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    selectedFeeForPayment?.let { fee ->
        CollectPaymentDialog(
            fee = fee,
            onConfirm = { amount, mode, ref, discount ->
                viewModel.recordFeePayment(fee.id, amount, mode, ref, discount)
                selectedFeeForPayment = null
            },
            onDismiss = { selectedFeeForPayment = null }
        )
    }

    selectedFeeForReceipt?.let { fee ->
        val student = students.find { it.id == fee.studentId }
        ReceiptDialog(
            fee = fee,
            student = student,
            onDismiss = { selectedFeeForReceipt = null }
        )
    }

    selectedFeeForReminder?.let { fee ->
        ReminderDialog(
            fee = fee,
            onReminderSent = {
                viewModel.markReminderSent(fee.id)
            },
            onDismiss = { selectedFeeForReminder = null }
        )
    }

    if (showCreateInvoiceDialog) {
        CreateFeeInvoiceDialog(
            students = students,
            onCreate = { student, period, feeType, amount, dueDate ->
                viewModel.createFeeRecord(student, period, feeType, amount, dueDate)
                showCreateInvoiceDialog = false
            },
            onDismiss = { showCreateInvoiceDialog = false }
        )
    }
}

@Composable
fun FeeRecordCard(
    fee: FeeRecordEntity,
    onCollectClick: () -> Unit,
    onReceiptClick: () -> Unit,
    onRemindClick: () -> Unit
) {
    val remaining = (fee.dueAmount - fee.paidAmount).coerceAtLeast(0.0)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Receipt # & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fee.receiptNo,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                StatusBadge(status = fee.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Student & Discipline
            Text(
                text = fee.studentName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "${fee.discipline} • ${fee.periodLabel}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Financial Summary Row
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Total Due", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = NrithyalayaUtils.formatCurrency(fee.dueAmount), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Column {
                        Text(text = "Paid Amount", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = NrithyalayaUtils.formatCurrency(fee.paidAmount), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF2E7D32))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Pending Balance", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = NrithyalayaUtils.formatCurrency(remaining),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (remaining > 0) Color(0xFFC62828) else Color(0xFF2E7D32)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (fee.status != "PAID") {
                    Button(
                        onClick = onCollectClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Collect", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onRemindClick,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Message, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", fontSize = 12.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick = onReceiptClick,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View & Share Digital Receipt")
                    }
                }
            }
        }
    }
}
