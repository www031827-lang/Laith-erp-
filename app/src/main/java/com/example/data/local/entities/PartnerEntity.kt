package com.example.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Partner Entity for Room Database.
 * Represents company equity partners, ownership percentages, withdrawal ceilings, and track records.
 */
@Entity(tableName = "partners")
data class PartnerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "partner_number")
    val partnerNumber: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "contribution_ratio")
    val contributionRatio: Double, // نسبة المساهمة %

    @ColumnInfo(name = "monthly_withdrawal_limit")
    val monthlyWithdrawalLimit: Double, // الحد الأقصى للسحب الشهري

    @ColumnInfo(name = "current_month_withdrawn")
    val currentMonthWithdrawn: Double = 0.0, // المسحوب هذا الشهر

    @ColumnInfo(name = "status")
    val status: String = "شريك نشط",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
