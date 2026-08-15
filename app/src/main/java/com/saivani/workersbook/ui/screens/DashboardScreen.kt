package com.saivani.workersbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.saivani.workersbook.data.WorkEntry
import com.saivani.workersbook.data.WorkerType
import com.saivani.workersbook.viewmodel.WorkersViewModel

@Composable
fun DashboardScreen(viewModel: WorkersViewModel, onAddEntry: () -> Unit) {
    val entries by viewModel.entriesForSelectedDate.collectAsState()
    val summary = viewModel.summaryFor(entries)
    val totalCost = entries.sumOf { it.totalAmount }
    val totalPeople = entries.sumOf { it.numPeople }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEntry) { Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_entry)) }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(stringResource(R.string.farm_name), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    StatChip(stringResource(R.string.male), summary[WorkerType.MALE]?.first ?: 0, Modifier.weight(1f))
                    StatChip(stringResource(R.string.female), summary[WorkerType.FEMALE]?.first ?: 0, Modifier.weight(1f))
                    StatChip(stringResource(R.string.contract), summary[WorkerType.CONTRACT]?.first ?: 0, Modifier.weight(1f))
                    StatChip(stringResource(R.string.part_time), summary[WorkerType.PART_TIME]?.first ?: 0, Modifier.weight(1f))
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    SummaryCard(stringResource(R.string.total_active_workers), totalPeople.toString(), Modifier.weight(1f))
                    SummaryCard(stringResource(R.string.total_labour_cost), "₹${totalCost.toInt()}", Modifier.weight(1f))
                }
            }

            item {
                Text(stringResource(R.string.todays_work_entries), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp))
            }

            items(entries) { entry -> EntryRow(entry) }

            if (entries.isEmpty()) {
                item { Text("No entries for this date yet.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@Composable
fun StatChip(label: String, value: Int, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun EntryRow(entry: WorkEntry) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(entry.workType, fontWeight = FontWeight.Bold)
                Text("${entry.numPeople} people", style = MaterialTheme.typography.bodySmall)
            }
            Text("₹${entry.totalAmount.toInt()}", fontWeight = FontWeight.Bold)
        }
    }
}
