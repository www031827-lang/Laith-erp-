package com.example.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.models.CashboxType
import com.example.data.models.MovementType

/**
 * Cashbox Transaction Entity for Room Database.
 * Represents all cash inflows and outflows across General Manager & Financial cashboxes.
 */
@Entity(tableName = "cashbox_transactions")
data class CashboxTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "transaction_number")
    val transactionNumber: String,

    @ColumnInfo(name = "movement_type")
    val movementType: MovementType,

    @ColumnInfo(name = "cashbox_type")
    val cashboxType: CashboxType,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "party_name")
    val partyName: String,

    @ColumnInfo(name = "party_type")
    val partyType: String, // عميل، عامل، شريك، مورد، مصروف، مشتريات

    @ColumnInfo(name = "user_role")
    val userRole: String,

    @ColumnInfo(name = "notes")
    val notes: String = "",

    @ColumnInfo(name = "balance_after")
    val balanceAfter: Double = 0.0,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
