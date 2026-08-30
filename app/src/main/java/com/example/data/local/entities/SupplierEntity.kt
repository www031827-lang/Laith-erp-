package com.example.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Supplier Entity for Room Database.
 * Represents raw material & equipment vendors and accounts payable balance.
 */
@Entity(tableName = "suppliers")
data class SupplierEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "supplier_number")
    val supplierNumber: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "company_name")
    val companyName: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "balance_due")
    val balanceDue: Double, // مستحقات المورد علينا

    @ColumnInfo(name = "status")
    val status: String = "نشط",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
