package com.saivani.workersbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saivani.workersbook.R
import com.saivani.workersbook.data.PaymentStatus
import com.saivani.workersbook.data.SalaryPayment
import com.saivani.workersbook.viewmodel.WorkersViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermanentWorkerDetailScreen(viewModel: WorkersViewModel, workerId: Long, onBack: () -> Unit) {
    val workers by viewModel.permanentWorkers.collectAsState()
    val worker = workers.find { it.id == workerId }
    val payments by viewModel.paymentsForWorker(workerId).collectAsState(initial = emptyList())

    val monthFormat = remember { SimpleDateFormat("yyyy-MM", Locale.US) }
    var month by remember { mutableStateOf(monthFormat.format(Date())) }
    var showPayDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(worker?.name ?: "") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            worker?.let {
                Card {
                    Column(Modifier.padding(14.dp)) {
                        Text(it.role, style = MaterialTheme.typography.bodyMedium)
                        Text("${stringResource(R.string.monthly_salary)}: ₹${it.monthlySalary.toInt()}", fontWeight = FontWeight.Bold)
                        it.phone?.let { p -> Text(p, style = MaterialTheme.typography.bodySmall) }
                    }
                }

                Button(onClick = { showPayDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.pay_salary))
                }

                Text("Payment History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                payments.forEach { p ->
                    Card {
                        Row(
                            Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(p.month, fontWeight = FontWeight.Medium)
                                Text(p.paymentMode, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(
                                "₹${p.amount.toInt()} · ${if (p.status == PaymentStatus.PAID) stringResource(R.string.paid) else stringResource(R.string.pending)}",
                                color = if (p.status == PaymentStatus.PAID) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                if (payments.isEmpty()) {
                    Text("No salary payments recorded yet.")
                }

                if (showPayDialog) {
                    PaySalaryDialog(
                        defaultAmount = it.monthlySalary,
                        defaultMonth = month,
                        onDismiss = { showPayDialog = false },
                        onConfirm = { payment ->
                            viewModel.paySalary(payment.copy(permanentWorkerId = workerId)) { showPayDialog = false }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaySalaryDialog(defaultAmount: Double, defaultMonth: String, onDismiss: () -> Unit, onConfirm: (SalaryPayment) -> Unit) {
    var month by remember { mutableStateOf(defaultMonth) }
    var amount by remember { mutableStateOf(defaultAmount.toInt().toString()) }
    var mode by remember { mutableStateOf("Bank Transfer") }
    val modes = listOf("Bank Transfer", "UPI", "Cash")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.pay_salary)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = month, onValueChange = { month = it }, label = { Text(stringResource(R.string.month) + " (yyyy-MM)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = amount, onValueChange = { amount = it.filter { c -> c.isDigit() } }, label = { Text(stringResource(R.string.amount)) }, modifier = Modifier.fillMaxWidth())
                Text(stringResource(R.string.payment_mode))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    modes.forEach { m ->
                        FilterChip(selected = mode == m, onClick = { mode = m }, label = { Text(m) })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    SalaryPayment(
                        permanentWorkerId = 0, // overwritten by caller
                        month = month,
                        amount = amount.toDoubleOrNull() ?: defaultAmount,
                        paymentMode = mode,
                        status = com.saivani.workersbook.data.PaymentStatus.PAID,
                        paidDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                    )
                )
            }) { Text(stringResource(R.string.confirm_payment)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}
