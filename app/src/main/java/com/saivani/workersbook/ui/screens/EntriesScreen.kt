package com.saivani.workersbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.saivani.workersbook.R
import com.saivani.workersbook.data.WorkerType
import com.saivani.workersbook.viewmodel.WorkersViewModel

@Composable
fun EntriesScreen(viewModel: WorkersViewModel, onAddEntry: () -> Unit) {
    val entries by viewModel.entriesForSelectedDate.collectAsState()
    var filter by remember { mutableStateOf<WorkerType?>(null) }

    val filtered = remember(entries, filter) {
        if (filter == null) entries else entries.filter { it.workerType == filter }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.entries_title)) }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAddEntry, icon = { Icon(Icons.Filled.Add, null) }, text = { Text(stringResource(R.string.add_entry)) })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = filter == null, onClick = { filter = null }, label = { Text(stringResource(R.string.filter_all)) })
                FilterChip(selected = filter == WorkerType.MALE, onClick = { filter = WorkerType.MALE }, label = { Text(stringResource(R.string.male)) })
                FilterChip(selected = filter == WorkerType.FEMALE, onClick = { filter = WorkerType.FEMALE }, label = { Text(stringResource(R.string.female)) })
                FilterChip(selected = filter == WorkerType.CONTRACT, onClick = { filter = WorkerType.CONTRACT }, label = { Text(stringResource(R.string.contract)) })
                FilterChip(selected = filter == WorkerType.PART_TIME, onClick = { filter = WorkerType.PART_TIME }, label = { Text(stringResource(R.string.part_time)) })
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered) { entry -> EntryRow(entry) }
                if (filtered.isEmpty()) {
                    item { Text("No entries found.", modifier = Modifier.padding(16.dp)) }
                }
                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }
}
