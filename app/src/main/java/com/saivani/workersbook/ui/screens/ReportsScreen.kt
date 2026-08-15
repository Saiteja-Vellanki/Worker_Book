package com.saivani.workersbook.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saivani.workersbook.R
import com.saivani.workersbook.data.WorkerType
import com.saivani.workersbook.viewmodel.WorkersViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: WorkersViewModel) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val range by viewModel.reportRange.collectAsState()
    val entries by viewModel.reportEntries.collectAsState()

    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }
    val fromState = rememberDatePickerState()
    val toState = rememberDatePickerState()

    val summary = viewModel.summaryFor(entries)
    val totalExpense = entries.sumOf { it.totalAmount }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.reports_title)) }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = range.first, onValueChange = {}, readOnly = true,
                    label = { Text(stringResource(R.string.from)) },
                    modifier = Modifier.weight(1f).androidxClickable { showFromPicker = true }
                )
                OutlinedTextField(
                    value = range.second, onValueChange = {}, readOnly = true,
                    label = { Text(stringResource(R.string.to)) },
                    modifier = Modifier.weight(1f).androidxClickable { showToPicker = true }
                )
            }

            Text(stringResource(R.string.summary), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            SummaryRow(stringResource(R.string.male), summary[WorkerType.MALE]?.second ?: 0.0)
            SummaryRow(stringResource(R.string.female), summary[WorkerType.FEMALE]?.second ?: 0.0)
            SummaryRow(stringResource(R.string.contract), summary[WorkerType.CONTRACT]?.second ?: 0.0)
            SummaryRow(stringResource(R.string.part_time), summary[WorkerType.PART_TIME]?.second ?: 0.0)

            Divider()

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.total_worker_expense), fontWeight = FontWeight.Bold)
                Text("₹${totalExpense.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            Text("Detailed Entries", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(entries) { entry ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("${entry.date} · ${labelFor(entry.workerType)}", style = MaterialTheme.typography.bodyMedium)
                            Text(entry.workType, style = MaterialTheme.typography.bodySmall)
                        }
                        Text("₹${entry.totalAmount.toInt()}")
                    }
                }
                if (entries.isEmpty()) {
                    item { Text("No entries in this range.") }
                }
            }
        }
    }

    if (showFromPicker) {
        DatePickerDialog(
            onDismissRequest = { showFromPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    fromState.selectedDateMillis?.let { viewModel.setReportRange(dateFormat.format(Date(it)), range.second) }
                    showFromPicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = fromState) }
    }
    if (showToPicker) {
        DatePickerDialog(
            onDismissRequest = { showToPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    toState.selectedDateMillis?.let { viewModel.setReportRange(range.first, dateFormat.format(Date(it))) }
                    showToPicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = toState) }
    }
}

@Composable
private fun SummaryRow(label: String, amount: Double) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label)
        Text("₹${amount.toInt()}", fontWeight = FontWeight.Medium)
    }
}

private fun Modifier.androidxClickable(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)
