package com.saivani.workersbook.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saivani.workersbook.R
import com.saivani.workersbook.data.PermanentWorker
import com.saivani.workersbook.viewmodel.WorkersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermanentWorkersScreen(viewModel: WorkersViewModel, onOpenWorker: (Long) -> Unit) {
    val workers by viewModel.permanentWorkers.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.permanent_workers)) }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { showAddDialog = true }, icon = { Icon(Icons.Filled.Add, null) }, text = { Text(stringResource(R.string.add_worker)) })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(workers) { worker ->
                Card(modifier = Modifier.fillMaxWidth().clickable { onOpenWorker(worker.id) }) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(worker.name, fontWeight = FontWeight.Bold)
                            Text(worker.role, style = MaterialTheme.typography.bodySmall)
                            Text("₹${worker.monthlySalary.toInt()} / month", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            if (workers.isEmpty()) {
                item { Text("No permanent workers added yet.", modifier = Modifier.padding(16.dp)) }
            }
            item { Spacer(Modifier.height(72.dp)) }
        }
    }

    if (showAddDialog) {
        AddPermanentWorkerDialog(
            onDismiss = { showAddDialog = false },
            onSave = { worker ->
                viewModel.addPermanentWorker(worker) { showAddDialog = false }
            }
        )
    }
}

@Composable
fun AddPermanentWorkerDialog(onDismiss: () -> Unit, onSave: (PermanentWorker) -> Unit) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_worker)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role (Supervisor, Watchman...)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = salary, onValueChange = { salary = it.filter { c -> c.isDigit() } }, label = { Text(stringResource(R.string.monthly_salary)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone (optional)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        PermanentWorker(
                            name = name.ifBlank { "Unnamed" },
                            role = role.ifBlank { "Worker" },
                            monthlySalary = salary.toDoubleOrNull() ?: 0.0,
                            phone = phone.ifBlank { null }
                        )
                    )
                },
                enabled = name.isNotBlank() && salary.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}
