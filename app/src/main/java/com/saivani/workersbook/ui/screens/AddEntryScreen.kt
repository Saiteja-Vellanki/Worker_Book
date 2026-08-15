package com.saivani.workersbook.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.saivani.workersbook.R
import com.saivani.workersbook.data.WorkEntry
import com.saivani.workersbook.data.WorkerType
import com.saivani.workersbook.viewmodel.WorkersViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryScreen(viewModel: WorkersViewModel, onSaved: () -> Unit, onCancel: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    var date by remember { mutableStateOf(dateFormat.format(Date())) }
    var type by remember { mutableStateOf(WorkerType.MALE) }

    var numPeople by remember { mutableStateOf("") }
    var pricePerUnit by remember { mutableStateOf("") }
    var workType by remember { mutableStateOf("") }
    var contractorName by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    // Auto-calc total: for CONTRACT, pricePerUnit field is used directly as the contract amount.
    val totalAmount = remember(numPeople, pricePerUnit, type) {
        val people = numPeople.toIntOrNull() ?: 0
        val price = pricePerUnit.toDoubleOrNull() ?: 0.0
        if (type == WorkerType.CONTRACT) price else people * price
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_entry)) },
                navigationIcon = { IconButton(onClick = onCancel) { Icon(Icons.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = date, onValueChange = {}, readOnly = true,
                label = { Text(stringResource(R.string.date)) },
                modifier = Modifier.fillMaxWidth().clickableField { showDatePicker = true }
            )

            Text(stringResource(R.string.worker_type), style = MaterialTheme.typography.labelLarge)
            WorkerTypeSelector(selected = type, onSelect = { type = it })

            OutlinedTextField(
                value = numPeople, onValueChange = { numPeople = it.filter { c -> c.isDigit() } },
                label = { Text(if (type == WorkerType.CONTRACT) "No. of Contracts" else stringResource(R.string.no_of_people)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            if (type == WorkerType.CONTRACT) {
                OutlinedTextField(
                    value = contractorName, onValueChange = { contractorName = it },
                    label = { Text(stringResource(R.string.contractor_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pricePerUnit, onValueChange = { pricePerUnit = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text(stringResource(R.string.contract_amount)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                OutlinedTextField(
                    value = pricePerUnit, onValueChange = { pricePerUnit = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text(if (type == WorkerType.PART_TIME) stringResource(R.string.price_per_hour) else stringResource(R.string.price_per_person)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = "₹${totalAmount.toInt()}", onValueChange = {}, readOnly = true,
                label = { Text(stringResource(R.string.total_amount)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = workType, onValueChange = { workType = it },
                label = { Text(stringResource(R.string.work_type)) },
                placeholder = { Text("e.g. Field Work, Harvesting, Spraying") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = note, onValueChange = { note = it },
                label = { Text(stringResource(R.string.note)) },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = {
                    val people = numPeople.toIntOrNull() ?: 0
                    val price = pricePerUnit.toDoubleOrNull() ?: 0.0
                    viewModel.saveEntry(
                        WorkEntry(
                            date = date,
                            workerType = type,
                            numPeople = if (people == 0) 1 else people,
                            pricePerUnit = price,
                            totalAmount = totalAmount,
                            workType = workType.ifBlank { "General" },
                            contractorName = contractorName.ifBlank { null },
                            note = note.ifBlank { null }
                        ),
                        onSaved = onSaved
                    )
                },
                enabled = totalAmount > 0,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(stringResource(R.string.save_entry), fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        date = dateFormat.format(Date(millis))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel)) } }
        ) { DatePicker(state = datePickerState) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerTypeSelector(selected: WorkerType, onSelect: (WorkerType) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        WorkerType.entries.forEach { type ->
            FilterChip(
                selected = selected == type,
                onClick = { onSelect(type) },
                label = { Text(labelFor(type)) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

fun labelFor(type: WorkerType): String = when (type) {
    WorkerType.MALE -> "Male"
    WorkerType.FEMALE -> "Female"
    WorkerType.CONTRACT -> "Contract"
    WorkerType.PART_TIME -> "Part-Time"
}

// Small helper: makes a read-only OutlinedTextField act like a clickable button (opens the date picker)
private fun Modifier.clickableField(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)
