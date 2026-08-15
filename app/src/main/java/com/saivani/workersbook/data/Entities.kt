package com.saivani.workersbook.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** The four worker categories shown on the dashboard. */
enum class WorkerType {
    MALE, FEMALE, CONTRACT, PART_TIME
}

enum class PaymentStatus { PENDING, PAID }

/**
 * A single day's work entry for daily-wage / contract / part-time workers.
 * Permanent (monthly salary) workers are modelled separately below.
 */
@Entity(tableName = "entries")
data class WorkEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,                 // yyyy-MM-dd
    val workerType: WorkerType,
    val numPeople: Int,                // for CONTRACT this is usually 1 (one contract/group)
    val pricePerUnit: Double,          // price/person or price/hour depending on type
    val totalAmount: Double,
    val workType: String,              // e.g. "Field Work", "Harvesting"
    val contractorName: String? = null, // only for CONTRACT
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "permanent_workers")
data class PermanentWorker(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val role: String,                  // e.g. Supervisor, Watchman, Farm Manager
    val monthlySalary: Double,
    val phone: String? = null,
    val bankAccountNo: String? = null,
    val ifscCode: String? = null,
    val joinDate: String? = null
)

@Entity(tableName = "salary_payments")
data class SalaryPayment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val permanentWorkerId: Long,
    val month: String,                 // yyyy-MM
    val amount: Double,
    val paymentMode: String,           // Bank Transfer / UPI / Cash
    val referenceId: String? = null,
    val status: PaymentStatus,
    val paidDate: String? = null,
    val note: String? = null
)
