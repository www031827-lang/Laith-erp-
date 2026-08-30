package com.example.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Customer Entity for Room Database.
 * Represents clients/customers with their credit limits, outstanding balances, and status.
 */
@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "customer_number")
    val customerNumber: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "credit_limit")
    val creditLimit: Double,

    @ColumnInfo(name = "balance")
    val balance: Double, // Positive = customer owes us (مديونية)

    @ColumnInfo(name = "status")
    val status: String = "نشط",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
