package com.saivani.workersbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saivani.workersbook.data.*
import com.saivani.workersbook.repository.WorkersRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WorkersViewModel(private val repo: WorkersRepository) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val _selectedDate = MutableStateFlow(dateFormat.format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate

    val entriesForSelectedDate: StateFlow<List<WorkEntry>> = _selectedDate
        .flatMapLatest { date -> repo.entriesForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val permanentWorkers: StateFlow<List<PermanentWorker>> = repo.allPermanentWorkers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _reportRange = MutableStateFlow(dateFormat.format(Date()) to dateFormat.format(Date()))
    val reportRange: StateFlow<Pair<String, String>> = _reportRange

    val reportEntries: StateFlow<List<WorkEntry>> = _reportRange
        .flatMapLatest { (from, to) -> repo.entriesInRange(from, to) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedDate(date: String) { _selectedDate.value = date }

    fun setReportRange(from: String, to: String) { _reportRange.value = from to to }

    fun saveEntry(entry: WorkEntry, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            repo.addEntry(entry)
            onSaved()
        }
    }

    fun deleteEntry(entry: WorkEntry) {
        viewModelScope.launch { repo.deleteEntry(entry) }
    }

    fun addPermanentWorker(worker: PermanentWorker, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            repo.addPermanentWorker(worker)
            onSaved()
        }
    }

    fun paySalary(payment: SalaryPayment, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            repo.addSalaryPayment(payment)
            onSaved()
        }
    }

    fun paymentsForWorker(workerId: Long) = repo.paymentsForWorker(workerId)

    /** Dashboard-style summary: count of unique people active today per category + total cost. */
    fun summaryFor(entries: List<WorkEntry>): Map<WorkerType, Pair<Int, Double>> {
        val map = mutableMapOf<WorkerType, Pair<Int, Double>>()
        for (type in WorkerType.entries) {
            val forType = entries.filter { it.workerType == type }
            val people = forType.sumOf { it.numPeople }
            val cost = forType.sumOf { it.totalAmount }
            map[type] = people to cost
        }
        return map
    }

    companion object {
        fun factory(repo: WorkersRepository) = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return WorkersViewModel(repo) as T
            }
        }
    }
}
