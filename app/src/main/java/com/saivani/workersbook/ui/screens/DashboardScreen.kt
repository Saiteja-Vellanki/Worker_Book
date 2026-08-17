package com.saivani.workersbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val permanentWorkers by viewModel.permanentWorkers.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val summary = viewModel.summaryFor(entries)
    val totalCost = entries.sumOf { it.totalAmount }
    val totalPeople = entries.sumOf { it.numPeople } + permanentWorkers.size

    val storageFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val displayFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.US) }
    val displayDate = remember(selectedDate) {
        runCatching { displayFormat.format(storageFormat.parse(selectedDate)!!) }.getOrDefault(selectedDate)
    }

    Scaffold(
        containerColor = SurfaceGray,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEntry,
                containerColor = BrandGreen,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp, pressedElevation = 10.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_entry))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ---- Header ----
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(BrandGreen, BrandGreenDark)))
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Menu, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                stringResource(R.string.dashboard_title),
                                color = Color.White,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier.size(38.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Notifications, contentDescription = stringResource(R.string.notifications), tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.farm_name), color = Color.White.copy(alpha = 0.92f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = Color.White.copy(alpha = 0.92f))
                    }
                }
            }

            // ---- Floating date navigator card ----
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-18).dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { shiftDate(viewModel, storageFormat, selectedDate, -1) }) {
                            Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous day", tint = BrandGreen)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(displayDate, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                        }
                        IconButton(onClick = { shiftDate(viewModel, storageFormat, selectedDate, 1) }) {
                            Icon(Icons.Filled.ChevronRight, contentDescription = "Next day", tint = BrandGreen)
                        }
                    }
                }
            }

            // ---- Category stat cards: row 1 ----
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-8).dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CategoryStatCard(
                        icon = Icons.Filled.Groups, iconColor = PermanentGreen,
                        label = stringResource(R.string.permanent), value = permanentWorkers.size.toString(), suffix = stringResource(R.string.active),
                        modifier = Modifier.weight(1f)
                    )
                    CategoryStatCard(
                        icon = Icons.Filled.Person, iconColor = FemalePink,
                        label = stringResource(R.string.female), value = (summary[WorkerType.FEMALE]?.first ?: 0).toString(), suffix = stringResource(R.string.active),
                        modifier = Modifier.weight(1f)
                    )
                    CategoryStatCard(
                        icon = Icons.Filled.Person, iconColor = MaleBlue,
                        label = stringResource(R.string.male), value = (summary[WorkerType.MALE]?.first ?: 0).toString(), suffix = stringResource(R.string.active),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ---- Category stat cards: row 2 ----
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CategoryStatCard(
                        icon = Icons.Filled.Handshake, iconColor = ContractOrange,
                        label = stringResource(R.string.contract), value = (summary[WorkerType.CONTRACT]?.first ?: 0).toString(), suffix = stringResource(R.string.active),
                        modifier = Modifier.weight(1f)
                    )
                    CategoryStatCard(
                        icon = Icons.Filled.Schedule, iconColor = PartTimePurple,
                        label = stringResource(R.string.part_time), value = (summary[WorkerType.PART_TIME]?.first ?: 0).toString(), suffix = stringResource(R.string.active),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // ---- Summary cards (gradient, premium look) ----
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GradientSummaryCard(
                        icon = Icons.Filled.Groups2,
                        label = stringResource(R.string.total_active_workers),
                        value = totalPeople.toString(),
                        colors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20)),
                        modifier = Modifier.weight(1f)
                    )
                    GradientSummaryCard(
                        icon = Icons.Filled.Payments,
                        label = stringResource(R.string.total_labour_cost),
                        value = "₹${totalCost.toInt()}",
                        colors = listOf(Color(0xFF1565C0), Color(0xFF0D47A1)),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ---- Today's Work Entries ----
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp).padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.todays_work_entries), fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.view_all), color = BrandGreen, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(16.dp))
                    }
                }
            }

            item {
                if (entries.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.EventNote, contentDescription = null, tint = TextSecondary.copy(alpha = 0.5f), modifier = Modifier.size(36.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("No entries for this date yet", color = TextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center)
                            Text("Tap the + button to add one", color = TextSecondary.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            entries.forEachIndexed { index, entry ->
                                DashboardEntryRow(entry)
                                if (index != entries.lastIndex) {
                                    Divider(color = SurfaceGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 14.dp))
                                }
                            }
                        }
                    }
                }
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
        modifier = modifier.shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = iconColor.copy(alpha = 0.25f)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(3.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(suffix, fontSize = 10.sp, color = iconColor, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun GradientSummaryCard(icon: ImageVector, label: String, value: String, colors: List<Color>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), spotColor = colors.first().copy(alpha = 0.4f))
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(colors, start = Offset(0f, 0f), end = Offset(300f, 300f)))
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier.size(30.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(value, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun DashboardEntryRow(entry: WorkEntry) {
    val (icon, color) = iconAndColorFor(entry.workerType)
    val timeText = remember(entry.createdAt) { SimpleDateFormat("h:mm a", Locale.US).format(Date(entry.createdAt)) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(42.dp).clip(CircleShape).background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(21.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(labelFor(entry.workerType) + " Workers", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
            Text(entry.workType, fontSize = 12.sp, color = TextSecondary)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("₹${entry.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            Text(
                "${entry.numPeople} ${if (entry.workerType == WorkerType.CONTRACT) "Contract" else "People"} · $timeText",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

private fun iconAndColorFor(type: WorkerType): Pair<ImageVector, Color> = when (type) {
    WorkerType.MALE -> Icons.Filled.Person to MaleBlue
    WorkerType.FEMALE -> Icons.Filled.Person to FemalePink
    WorkerType.CONTRACT -> Icons.Filled.Handshake to ContractOrange
    WorkerType.PART_TIME -> Icons.Filled.Schedule to PartTimePurple
}
