package com.saivani.workersbook.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromWorkerType(value: WorkerType): String = value.name

    @TypeConverter
    fun toWorkerType(value: String): WorkerType = WorkerType.valueOf(value)

    @TypeConverter
    fun fromPaymentStatus(value: PaymentStatus): String = value.name

    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus = PaymentStatus.valueOf(value)
}
