package com.example.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Worker Entity for Room Database.
 * Represents factory & company workers, job titles, base salaries, accruals, and withdrawals.
 */
@Entity(tableName = "workers")
data class WorkerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "worker_number")
    val workerNumber: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "job_title")
    val jobTitle: String,

    @ColumnInfo(name = "basic_salary")
    val basicSalary: Double,

    @ColumnInfo(name = "accrued_amount")
    val accruedAmount: Double, // المستحق الحالي

    @ColumnInfo(name = "total_withdrawn")
    val totalWithdrawn: Double, // إجمالي المسحوبات

    @ColumnInfo(name = "status")
    val status: String = "على رأس العمل",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
