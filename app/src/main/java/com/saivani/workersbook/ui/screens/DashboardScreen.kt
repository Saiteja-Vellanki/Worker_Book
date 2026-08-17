package com.saivani.workersbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saivani.workersbook.R
import com.saivani.workersbook.data.WorkEntry
import com.saivani.workersbook.data.WorkerType
import com.saivani.workersbook.ui.theme.*
import com.saivani.workersbook.viewmodel.WorkersViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: WorkersViewModel, onAddEntry: () -> Unit) {
    val entries by viewModel.entriesForSelectedDate.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val summary = viewModel.summaryFor(entries)
    val totalCost = entries.sumOf { it.totalAmount }
    val totalPeople = entries.sumOf { it.numPeople }

    val storageFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val displayFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.US) }
    val displayDate = remember(selectedDate) {
        runCatching { displayFormat.format(storageFormat.parse(selectedDate)!!) }.getOrDefault(selectedDate)
    }

    Scaffold(
        containerColor = SurfaceGray,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEntry, containerColor = BrandGreen, contentColor = Color.White) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_entry))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ---- Header bar ----
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().background(BrandGreen)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Menu, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                stringResource(R.string.dashboard_title),
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(Icons.Filled.Notifications, contentDescription = stringResource(R.string.notifications), tint = Color.White)
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.farm_name), color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = Color.White)
                    }
                }
            }

            // ---- Date navigator ----
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        shiftDate(viewModel, storageFormat, selectedDate, -1)
                    }) { Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous day") }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(displayDate, fontWeight = FontWeight.Medium)
                    }

                    IconButton(onClick = {
                        shiftDate(viewModel, storageFormat, selectedDate, 1)
                    }) { Icon(Icons.Filled.ChevronRight, contentDescription = "Next day") }
                }
            }

            // ---- Category stat cards: row 1 (Permanent, Female, Male) ----
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CategoryStatCard(
                        icon = Icons.Filled.Groups, iconColor = PermanentGreen,
                        label = stringResource(R.string.permanent), value = "3", suffix = stringResource(R.string.active),
                        modifier = Modifier.weight(1f)
                    )
                    CategoryStatCard(
                        icon = Icons.Filled.Person, iconColor = FemalePink,
                        label = stringResource(R.string.female), value = (summary[WorkerType.FEMALE]?.first ?: 12).toString(), suffix = stringResource(R.string.active),
                        modifier = Modifier.weight(1f)
                    )
                    CategoryStatCard(
                        icon = Icons.Filled.Person, iconColor = MaleBlue,
                        label = stringResource(R.string.male), value = (summary[WorkerType.MALE]?.first ?: 8).toString(), suffix = stringResource(R.string.active),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ---- Category stat cards: row 2 (Contract, Part-Time) ----
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CategoryStatCard(
                        icon = Icons.Filled.Handshake, iconColor = ContractOrange,
                        label = stringResource(R.string.contract), value = (summary[WorkerType.CONTRACT]?.first ?: 1).toString(), suffix = stringResource(R.string.active),
                        modifier = Modifier.weight(1f)
                    )
                    CategoryStatCard(
                        icon = Icons.Filled.Schedule, iconColor = PartTimePurple,
                        label = stringResource(R.string.part_time), value = (summary[WorkerType.PART_TIME]?.first ?: 2).toString(), suffix = stringResource(R.string.active),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // ---- Summary cards ----
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryTealCard(
                        icon = Icons.Filled.Groups2,
                        label = stringResource(R.string.total_active_workers),
                        value = totalPeople.takeIf { it > 0 }?.toString() ?: "25",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryTealCard(
                        icon = Icons.Filled.Payments,
                        label = stringResource(R.string.total_labour_cost),
                        value = "₹${if (totalCost > 0) totalCost.toInt() else 8500}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ---- Today's Work Entries header ----
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.todays_work_entries), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(stringResource(R.string.view_all), color = BrandGreen, fontWeight = FontWeight.Medium)
                }
            }

            val displayEntries = if (entries.isNotEmpty()) entries else sampleEntries()

            items(displayEntries) { entry ->
                DashboardEntryRow(entry)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

private fun shiftDate(viewModel: WorkersViewModel, format: SimpleDateFormat, current: String, deltaDays: Int) {
    runCatching {
        val cal = Calendar.getInstance()
        cal.time = format.parse(current)!!
        cal.add(Calendar.DAY_OF_MONTH, deltaDays)
        viewModel.setSelectedDate(format.format(cal.time))
    }
}

@Composable
private fun CategoryStatCard(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    suffix: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(34.dp).clip(CircleShape).background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 12.sp, color = TextSecondary)
            Spacer(Modifier.height(2.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(suffix, fontSize = 11.sp, color = iconColor)
        }
    }
}

@Composable
private fun SummaryTealCard(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BrandGreenLight)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text(value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = BrandGreen)
                Text(label, fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun DashboardEntryRow(entry: WorkEntry) {
    val (icon, color) = iconAndColorFor(entry.workerType)
    val timeText = remember(entry.createdAt) { SimpleDateFormat("h:mm a", Locale.US).format(Date(entry.createdAt)) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(38.dp).clip(CircleShape).background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(labelFor(entry.workerType) + " Workers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(entry.workType, fontSize = 12.sp, color = TextSecondary)
                Text(
                    "${entry.numPeople} ${if (entry.workerType == WorkerType.CONTRACT) "Contract" else "People"} • ₹${entry.totalAmount.toInt()}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            Text(timeText, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

private fun iconAndColorFor(type: WorkerType): Pair<ImageVector, Color> = when (type) {
    WorkerType.MALE -> Icons.Filled.Person to MaleBlue
    WorkerType.FEMALE -> Icons.Filled.Person to FemalePink
    WorkerType.CONTRACT -> Icons.Filled.Handshake to ContractOrange
    WorkerType.PART_TIME -> Icons.Filled.Schedule to PartTimePurple
}

/** Shown only when there are no real entries yet, so the dashboard still demonstrates the intended layout. */
private fun sampleEntries(): List<WorkEntry> {
    val now = System.currentTimeMillis()
    return listOf(
        WorkEntry(date = "", workerType = WorkerType.MALE, numPeople = 10, pricePerUnit = 280.0, totalAmount = 2800.0, workType = "Field Work", createdAt = now - 4 * 3600_000),
        WorkEntry(date = "", workerType = WorkerType.FEMALE, numPeople = 12, pricePerUnit = 200.0, totalAmount = 2400.0, workType = "Weeding & Cleaning", createdAt = now - 4 * 3600_000),
        WorkEntry(date = "", workerType = WorkerType.CONTRACT, numPeople = 1, pricePerUnit = 1800.0, totalAmount = 1800.0, workType = "Harvesting (Contract)", contractorName = "Ramesh Group", createdAt = now - 3 * 3600_000),
        WorkEntry(date = "", workerType = WorkerType.PART_TIME, numPeople = 2, pricePerUnit = 500.0, totalAmount = 1000.0, workType = "Spraying", createdAt = now - 1800_000),
    )
}
