package com.saivani.workersbook.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkersDao {

    // ---- Entries (daily wage / contract / part-time) ----

    @Insert
    suspend fun insertEntry(entry: WorkEntry): Long

    @Update
    suspend fun updateEntry(entry: WorkEntry)

    @Delete
    suspend fun deleteEntry(entry: WorkEntry)

    @Query("SELECT * FROM entries WHERE date = :date ORDER BY createdAt DESC")
    fun entriesForDate(date: String): Flow<List<WorkEntry>>

    @Query("SELECT * FROM entries WHERE date BETWEEN :from AND :to ORDER BY date DESC, createdAt DESC")
    fun entriesInRange(from: String, to: String): Flow<List<WorkEntry>>

    @Query("SELECT * FROM entries ORDER BY date DESC, createdAt DESC LIMIT :limit")
    fun recentEntries(limit: Int = 20): Flow<List<WorkEntry>>

    // ---- Permanent workers ----

    @Insert
    suspend fun insertPermanentWorker(worker: PermanentWorker): Long

    @Update
    suspend fun updatePermanentWorker(worker: PermanentWorker)

    @Delete
    suspend fun deletePermanentWorker(worker: PermanentWorker)

    @Query("SELECT * FROM permanent_workers ORDER BY name ASC")
    fun allPermanentWorkers(): Flow<List<PermanentWorker>>

    @Query("SELECT * FROM permanent_workers WHERE id = :id")
    fun permanentWorkerById(id: Long): Flow<PermanentWorker?>

    // ---- Salary payments ----

    @Insert
    suspend fun insertSalaryPayment(payment: SalaryPayment): Long

    @Update
    suspend fun updateSalaryPayment(payment: SalaryPayment)

    @Query("SELECT * FROM salary_payments WHERE permanentWorkerId = :workerId ORDER BY month DESC")
    fun paymentsForWorker(workerId: Long): Flow<List<SalaryPayment>>

    @Query("SELECT * FROM salary_payments WHERE permanentWorkerId = :workerId AND month = :month LIMIT 1")
    suspend fun paymentForMonth(workerId: Long, month: String): SalaryPayment?
}
