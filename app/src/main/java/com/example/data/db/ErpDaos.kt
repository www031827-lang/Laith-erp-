package com.example.data.db

import androidx.room.*
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow

class Converters {
    @TypeConverter
    fun fromAppRole(role: AppRole): String = role.name

    @TypeConverter
    fun toAppRole(value: String): AppRole = try {
        AppRole.valueOf(value)
    } catch (e: Exception) {
        AppRole.GENERAL_MANAGER
    }

    @TypeConverter
    fun fromApprovalStatus(status: ApprovalStatus): String = status.name

    @TypeConverter
    fun toApprovalStatus(value: String): ApprovalStatus = try {
        ApprovalStatus.valueOf(value)
    } catch (e: Exception) {
        ApprovalStatus.PENDING
    }

    @TypeConverter
    fun fromCashboxType(type: CashboxType): String = type.name

    @TypeConverter
    fun toCashboxType(value: String): CashboxType = try {
        CashboxType.valueOf(value)
    } catch (e: Exception) {
        CashboxType.FINANCIAL
    }

    @TypeConverter
    fun fromMovementType(type: MovementType): String = type.name

    @TypeConverter
    fun toMovementType(value: String): MovementType = try {
        MovementType.valueOf(value)
    } catch (e: Exception) {
        MovementType.DEPOSIT
    }
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY id DESC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Long): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Query("UPDATE customers SET balance = balance - :amount WHERE id = :id")
    suspend fun reduceDebt(id: Long, amount: Double)

    @Query("UPDATE customers SET balance = balance + :amount WHERE id = :id")
    suspend fun addDebt(id: Long, amount: Double)

    @Query("DELETE FROM customers WHERE id = :id")
    suspend fun deleteCustomer(id: Long)
}

@Dao
interface WorkerDao {
    @Query("SELECT * FROM workers ORDER BY id DESC")
    fun getAllWorkers(): Flow<List<Worker>>

    @Query("SELECT * FROM workers WHERE id = :id")
    suspend fun getWorkerById(id: Long): Worker?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorker(worker: Worker): Long

    @Update
    suspend fun updateWorker(worker: Worker)

    @Query("UPDATE workers SET totalWithdrawn = totalWithdrawn + :amount, accruedAmount = accruedAmount - :amount WHERE id = :id")
    suspend fun recordWithdrawal(id: Long, amount: Double)

    @Query("DELETE FROM workers WHERE id = :id")
    suspend fun deleteWorker(id: Long)
}

@Dao
interface PartnerDao {
    @Query("SELECT * FROM partners ORDER BY id DESC")
    fun getAllPartners(): Flow<List<Partner>>

    @Query("SELECT * FROM partners WHERE id = :id")
    suspend fun getPartnerById(id: Long): Partner?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartner(partner: Partner): Long

    @Update
    suspend fun updatePartner(partner: Partner)

    @Query("UPDATE partners SET currentMonthWithdrawn = currentMonthWithdrawn + :amount WHERE id = :id")
    suspend fun recordPartnerWithdrawal(id: Long, amount: Double)

    @Query("DELETE FROM partners WHERE id = :id")
    suspend fun deletePartner(id: Long)
}

@Dao
interface SupplierDao {
    @Query("SELECT * FROM suppliers ORDER BY id DESC")
    fun getAllSuppliers(): Flow<List<Supplier>>

    @Query("SELECT * FROM suppliers WHERE id = :id")
    suspend fun getSupplierById(id: Long): Supplier?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: Supplier): Long

    @Update
    suspend fun updateSupplier(supplier: Supplier)

    @Query("UPDATE suppliers SET balanceDue = balanceDue - :amount WHERE id = :id")
    suspend fun paySupplier(id: Long, amount: Double)

    @Query("DELETE FROM suppliers WHERE id = :id")
    suspend fun deleteSupplier(id: Long)
}

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY id DESC")
    fun getAllItems(): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item): Long

    @Update
    suspend fun updateItem(item: Item)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteItem(id: Long)
}

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchases ORDER BY id DESC")
    fun getAllPurchases(): Flow<List<PurchaseOrder>>

    @Query("SELECT * FROM purchases WHERE status = :status ORDER BY id DESC")
    fun getPurchasesByStatus(status: ApprovalStatus): Flow<List<PurchaseOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: PurchaseOrder): Long

    @Query("UPDATE purchases SET status = :status, approvedDate = :date WHERE id = :id")
    suspend fun updateStatus(id: Long, status: ApprovalStatus, date: Long)
}

@Dao
interface CashboxDao {
    @Query("SELECT * FROM cashboxes")
    fun getAllCashboxes(): Flow<List<Cashbox>>

    @Query("SELECT * FROM cashboxes WHERE type = :type LIMIT 1")
    suspend fun getCashboxByType(type: CashboxType): Cashbox?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashbox(cashbox: Cashbox): Long

    @Query("UPDATE cashboxes SET currentBalance = currentBalance + :amount, lastUpdated = :timestamp WHERE type = :type")
    suspend fun deposit(type: CashboxType, amount: Double, timestamp: Long)

    @Query("UPDATE cashboxes SET currentBalance = currentBalance - :amount, lastUpdated = :timestamp WHERE type = :type")
    suspend fun withdraw(type: CashboxType, amount: Double, timestamp: Long)
}

@Dao
interface CashMovementDao {
    @Query("SELECT * FROM cash_movements ORDER BY timestamp DESC")
    fun getAllMovements(): Flow<List<CashMovement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(movement: CashMovement): Long
}

@Dao
interface SaleInvoiceDao {
    @Query("SELECT * FROM sale_invoices ORDER BY timestamp DESC")
    fun getAllInvoices(): Flow<List<SaleInvoice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: SaleInvoice): Long
}

@Dao
interface ProductionDao {
    @Query("SELECT * FROM production_records ORDER BY timestamp DESC")
    fun getAllProductionRecords(): Flow<List<ProductionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ProductionRecord): Long
}

@Dao
interface RawMaterialDao {
    @Query("SELECT * FROM raw_materials ORDER BY id DESC")
    fun getAllMaterials(): Flow<List<RawMaterial>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: RawMaterial): Long

    @Update
    suspend fun updateMaterial(material: RawMaterial)
}

@Dao
interface ImprovementDao {
    @Query("SELECT * FROM improvements ORDER BY requestedDate DESC")
    fun getAllImprovements(): Flow<List<ImprovementRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImprovement(improvement: ImprovementRecord): Long

    @Query("UPDATE improvements SET status = :status, approvedDate = :date WHERE id = :id")
    suspend fun updateStatus(id: Long, status: ApprovalStatus, date: Long)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY requestedDate DESC")
    fun getAllExpenses(): Flow<List<ExpenseRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseRecord): Long

    @Query("UPDATE expenses SET status = :status, approvedDate = :date WHERE id = :id")
    suspend fun updateStatus(id: Long, status: ApprovalStatus, date: Long)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user_accounts ORDER BY id ASC")
    fun getAllUsers(): Flow<List<UserAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount): Long

    @Update
    suspend fun updateUser(user: UserAccount)

    @Query("DELETE FROM user_accounts WHERE id = :id")
    suspend fun deleteUser(id: Long)
}

@Dao
interface AppReleaseDao {
    @Query("SELECT * FROM app_releases ORDER BY versionCode DESC")
    fun getAllReleases(): Flow<List<AppRelease>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelease(release: AppRelease): Long

    @Query("UPDATE app_releases SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun toggleEnabled(id: Long, isEnabled: Boolean)
}

@Dao
interface PrinterDao {
    @Query("SELECT * FROM printer_configs")
    fun getAllPrinters(): Flow<List<PrinterConfig>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrinter(printer: PrinterConfig): Long
}

@Dao
interface AuditDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLog): Long
}

@Dao
interface SyncDao {
    @Query("SELECT * FROM sync_queue ORDER BY timestamp DESC")
    fun getAllSyncItems(): Flow<List<SyncQueueItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncItem(item: SyncQueueItem): Long

    @Query("DELETE FROM sync_queue WHERE status = 'SYNCED'")
    suspend fun clearSynced()
}
