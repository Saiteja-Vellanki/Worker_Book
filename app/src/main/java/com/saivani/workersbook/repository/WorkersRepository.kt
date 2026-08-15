package com.saivani.workersbook.repository

import com.saivani.workersbook.data.*
import kotlinx.coroutines.flow.Flow

class WorkersRepository(private val dao: WorkersDao) {

    // Entries
    suspend fun addEntry(entry: WorkEntry) = dao.insertEntry(entry)
    suspend fun updateEntry(entry: WorkEntry) = dao.updateEntry(entry)
    suspend fun deleteEntry(entry: WorkEntry) = dao.deleteEntry(entry)
    fun entriesForDate(date: String): Flow<List<WorkEntry>> = dao.entriesForDate(date)
    fun entriesInRange(from: String, to: String): Flow<List<WorkEntry>> = dao.entriesInRange(from, to)
    fun recentEntries(limit: Int = 20): Flow<List<WorkEntry>> = dao.recentEntries(limit)

    // Permanent workers
    suspend fun addPermanentWorker(worker: PermanentWorker) = dao.insertPermanentWorker(worker)
    suspend fun updatePermanentWorker(worker: PermanentWorker) = dao.updatePermanentWorker(worker)
    suspend fun deletePermanentWorker(worker: PermanentWorker) = dao.deletePermanentWorker(worker)
    fun allPermanentWorkers(): Flow<List<PermanentWorker>> = dao.allPermanentWorkers()
    fun permanentWorkerById(id: Long): Flow<PermanentWorker?> = dao.permanentWorkerById(id)

    // Salary payments
    suspend fun addSalaryPayment(payment: SalaryPayment) = dao.insertSalaryPayment(payment)
    suspend fun updateSalaryPayment(payment: SalaryPayment) = dao.updateSalaryPayment(payment)
    fun paymentsForWorker(workerId: Long): Flow<List<SalaryPayment>> = dao.paymentsForWorker(workerId)
    suspend fun paymentForMonth(workerId: Long, month: String): SalaryPayment? = dao.paymentForMonth(workerId, month)
}
