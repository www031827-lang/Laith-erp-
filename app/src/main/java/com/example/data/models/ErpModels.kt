package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AppRole(val titleAr: String, val badgeColorHex: Long) {
    GENERAL_MANAGER("المدير العام", 0xFF1E3A8A),
    FINANCIAL_MANAGER("المدير المالي", 0xFF059669),
    SALES_MANAGER("مدير المبيعات", 0xFFD97706),
    RAW_MATERIALS_MANAGER("مسؤول المواد الخام", 0xFF0D9488),
    PRODUCTION_MANAGER("مسؤول الإنتاج", 0xFF7C3AED),
    AUDITOR("مراقب عام وتدقيق", 0xFF475569)
}

enum class ApprovalStatus(val titleAr: String) {
    PENDING("قيد الانتظار"),
    APPROVED("معتمد"),
    REJECTED("مرفوض")
}

enum class CashboxType(val titleAr: String) {
    GENERAL_MANAGER("خزينة المدير العام"),
    FINANCIAL("الخزينة المالية")
}

enum class MovementType(val titleAr: String) {
    DEPOSIT("إيداع / تحصيل"),
    WITHDRAWAL("صرف / سحب"),
    TRANSFER("تحويل بين الخزائن"),
    EXPENSE("مصروف معتمد"),
    PURCHASE("مشتريات معتمدة")
}

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerNumber: String,
    val name: String,
    val phone: String,
    val address: String,
    val creditLimit: Double,
    val balance: Double, // positive = customer owes us (مديونية)
    val status: String = "نشط",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "workers")
data class Worker(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workerNumber: String,
    val name: String,
    val phone: String,
    val jobTitle: String,
    val basicSalary: Double,
    val accruedAmount: Double, // المستحق الحالي
    val totalWithdrawn: Double, // إجمالي المسحوبات
    val status: String = "على رأس العمل",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "partners")
data class Partner(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val partnerNumber: String,
    val name: String,
    val phone: String,
    val contributionRatio: Double, // نسبة المساهمة %
    val monthlyWithdrawalLimit: Double, // الحد الأقصى للسحب الشهري
    val currentMonthWithdrawn: Double = 0.0, // المسحوب هذا الشهر
    val status: String = "شريك نشط",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val supplierNumber: String,
    val name: String,
    val phone: String,
    val companyName: String,
    val address: String,
    val balanceDue: Double, // مستحقات المورد علينا
    val status: String = "نشط",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemCode: String,
    val name: String,
    val category: String,
    val unit: String,
    val unitPrice: Double,
    val costPrice: Double,
    val currentStock: Double,
    val minStockAlert: Double = 10.0,
    val status: String = "متاح"
)

@Entity(tableName = "purchases")
data class PurchaseOrder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String,
    val itemName: String,
    val quantity: Double,
    val totalAmount: Double,
    val notes: String = "",
    val createdByRole: String = "المدير المالي",
    val status: ApprovalStatus = ApprovalStatus.PENDING,
    val requestedDate: Long = System.currentTimeMillis(),
    val approvedDate: Long? = null
)

@Entity(tableName = "cashboxes")
data class Cashbox(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val type: CashboxType,
    val currentBalance: Double,
    val currency: String = "YER",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "cash_movements")
data class CashMovement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val movementNumber: String,
    val type: MovementType,
    val cashboxType: CashboxType,
    val amount: Double,
    val partyName: String,
    val partyType: String, // عميل، عامل، شريك، مورد، مصروف، مشتريات
    val userRole: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sale_invoices")
data class SaleInvoice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val customerId: Long,
    val customerName: String,
    val totalAmount: Double,
    val paidAmount: Double,
    val remainingDebt: Double,
    val itemsSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "production_records")
data class ProductionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val batchNumber: String,
    val dateString: String,
    val dailyProductionValue: Double, // قيمة إنتاج اليوم
    val dailySalesValue: Double, // قيمة مبيعات اليوم
    val remainingProductionValue: Double, // المتبقي = إنتاج - مبيعات
    val outputQuantity: Double,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "raw_materials")
data class RawMaterial(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val materialCode: String,
    val name: String,
    val unit: String,
    val unitPrice: Double,
    val totalStockValue: Double, // قيمة المواد الخام الكلية
    val issuedValue: Double, // قيمة المواد الخام الصادرة
    val remainingStockValue: Double, // قيمة المواد الخام المتبقية
    val currentQuantity: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "improvements")
data class ImprovementRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val quantity: Double,
    val estimatedCost: Double,
    val status: ApprovalStatus = ApprovalStatus.PENDING,
    val requestedBy: String = "المدير المالي",
    val requestedDate: Long = System.currentTimeMillis(),
    val approvedDate: Long? = null
)

@Entity(tableName = "expenses")
data class ExpenseRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val expenseCategory: String,
    val description: String,
    val amount: Double,
    val status: ApprovalStatus = ApprovalStatus.PENDING,
    val requestedBy: String = "المدير المالي",
    val requestedDate: Long = System.currentTimeMillis(),
    val approvedDate: Long? = null
)

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val fullName: String,
    val role: AppRole,
    val permissions: String,
    val isActive: Boolean = true,
    val lastLogin: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_releases")
data class AppRelease(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val appName: String,
    val versionCode: Int,
    val versionName: String,
    val releaseNotes: String,
    val isMandatory: Boolean = false,
    val isEnabled: Boolean = true,
    val releasedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "printer_configs")
data class PrinterConfig(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val printerName: String,
    val address: String,
    val paperSize: String = "80mm Thermal",
    val isDefault: Boolean = true,
    val isConnected: Boolean = true
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val userName: String,
    val userRole: String,
    val applicationName: String,
    val action: String,
    val entityName: String,
    val entityId: String,
    val oldValue: String = "",
    val newValue: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sync_queue")
data class SyncQueueItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entityType: String,
    val action: String,
    val payloadJson: String,
    val status: String = "PENDING_OFFLINE",
    val retryCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
