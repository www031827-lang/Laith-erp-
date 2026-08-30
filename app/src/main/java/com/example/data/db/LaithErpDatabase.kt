package com.example.data.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Customer::class,
        Worker::class,
        Partner::class,
        Supplier::class,
        Item::class,
        PurchaseOrder::class,
        Cashbox::class,
        CashMovement::class,
        SaleInvoice::class,
        ProductionRecord::class,
        RawMaterial::class,
        ImprovementRecord::class,
        ExpenseRecord::class,
        UserAccount::class,
        AppRelease::class,
        PrinterConfig::class,
        AuditLog::class,
        SyncQueueItem::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LaithErpDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun workerDao(): WorkerDao
    abstract fun partnerDao(): PartnerDao
    abstract fun supplierDao(): SupplierDao
    abstract fun itemDao(): ItemDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun cashboxDao(): CashboxDao
    abstract fun cashMovementDao(): CashMovementDao
    abstract fun saleInvoiceDao(): SaleInvoiceDao
    abstract fun productionDao(): ProductionDao
    abstract fun rawMaterialDao(): RawMaterialDao
    abstract fun improvementDao(): ImprovementDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun userDao(): UserDao
    abstract fun appReleaseDao(): AppReleaseDao
    abstract fun printerDao(): PrinterDao
    abstract fun auditDao(): AuditDao
    abstract fun syncDao(): SyncDao

    companion object {
        @Volatile
        private var INSTANCE: LaithErpDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): LaithErpDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LaithErpDatabase::class.java,
                    "laith_erp_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        suspend fun populateInitialData(db: LaithErpDatabase) {
            val now = System.currentTimeMillis()

            // 1. Cashboxes
            val generalCashbox = Cashbox(
                code = "CB-01",
                name = "خزينة المدير العام",
                type = CashboxType.GENERAL_MANAGER,
                currentBalance = 4250000.0,
                lastUpdated = now
            )
            val financialCashbox = Cashbox(
                code = "CB-02",
                name = "الخزينة المالية (التحصيلات)",
                type = CashboxType.FINANCIAL,
                currentBalance = 2180000.0,
                lastUpdated = now
            )
            db.cashboxDao().insertCashbox(generalCashbox)
            db.cashboxDao().insertCashbox(financialCashbox)

            // 2. Customers
            val c1 = Customer(
                customerNumber = "CUST-101",
                name = "شركة الأمل للتجارة والتوزيع",
                phone = "770123456",
                address = "صنعاء - شارع الزبيري",
                creditLimit = 3000000.0,
                balance = 1450000.0,
                status = "نشط"
            )
            val c2 = Customer(
                customerNumber = "CUST-102",
                name = "مؤسسة البركة للمقاولات",
                phone = "771987654",
                address = "عدن - المعلا",
                creditLimit = 2000000.0,
                balance = 850000.0,
                status = "نشط"
            )
            val c3 = Customer(
                customerNumber = "CUST-103",
                name = "مركز النور للتوريدات",
                phone = "773456789",
                address = "تعز - الحوبان",
                creditLimit = 1500000.0,
                balance = 400000.0,
                status = "نشط"
            )
            val c4 = Customer(
                customerNumber = "CUST-104",
                name = "محلات السعادة النموذجية",
                phone = "775678123",
                address = "الحديدة - الميناء",
                creditLimit = 1000000.0,
                balance = 0.0,
                status = "نشط"
            )
            db.customerDao().insertCustomer(c1)
            db.customerDao().insertCustomer(c2)
            db.customerDao().insertCustomer(c3)
            db.customerDao().insertCustomer(c4)

            // 3. Workers
            val w1 = Worker(
                workerNumber = "WRK-001",
                name = "عمر أحمد الجابري",
                phone = "772111222",
                jobTitle = "فني تشغيل خطوط الإنتاج",
                basicSalary = 180000.0,
                accruedAmount = 95000.0,
                totalWithdrawn = 85000.0,
                status = "على رأس العمل"
            )
            val w2 = Worker(
                workerNumber = "WRK-002",
                name = "محمد سالم باوزير",
                phone = "772333444",
                jobTitle = "مسؤول مراقبة الجودة",
                basicSalary = 220000.0,
                accruedAmount = 140000.0,
                totalWithdrawn = 80000.0,
                status = "على رأس العمل"
            )
            val w3 = Worker(
                workerNumber = "WRK-003",
                name = "ياسين عبده صالح",
                phone = "772555666",
                jobTitle = "مشغل رافعة ومستودعات",
                basicSalary = 150000.0,
                accruedAmount = 60000.0,
                totalWithdrawn = 90000.0,
                status = "على رأس العمل"
            )
            db.workerDao().insertWorker(w1)
            db.workerDao().insertWorker(w2)
            db.workerDao().insertWorker(w3)

            // 4. Partners
            val p1 = Partner(
                partnerNumber = "PRT-01",
                name = "الشيخ عبد الله بن ناصر",
                phone = "777000111",
                contributionRatio = 45.0,
                monthlyWithdrawalLimit = 1200000.0,
                currentMonthWithdrawn = 400000.0,
                status = "شريك رئيسي"
            )
            val p2 = Partner(
                partnerNumber = "PRT-02",
                name = "م. خالد سعيد المنصوري",
                phone = "777222333",
                contributionRatio = 35.0,
                monthlyWithdrawalLimit = 900000.0,
                currentMonthWithdrawn = 300000.0,
                status = "شريك مؤسس"
            )
            val p3 = Partner(
                partnerNumber = "PRT-03",
                name = "أ. وليد عبد الرحمن القاضي",
                phone = "777444555",
                contributionRatio = 20.0,
                monthlyWithdrawalLimit = 600000.0,
                currentMonthWithdrawn = 150000.0,
                status = "شريك مساهم"
            )
            db.partnerDao().insertPartner(p1)
            db.partnerDao().insertPartner(p2)
            db.partnerDao().insertPartner(p3)

            // 5. Suppliers
            val s1 = Supplier(
                supplierNumber = "SUP-501",
                name = "شركة الخليج للبتروكيماويات",
                phone = "778111999",
                companyName = "Gulf Petrochemicals Ltd",
                address = "صنعاء - شارع الستين",
                balanceDue = 1850000.0,
                status = "نشط"
            )
            val s2 = Supplier(
                supplierNumber = "SUP-502",
                name = "مصنع الشرق لمواد التعبئة والتغليف",
                phone = "778222888",
                companyName = "Orient Packaging Factory",
                address = "عدن - المنطقة الصناعية",
                balanceDue = 920000.0,
                status = "نشط"
            )
            val s3 = Supplier(
                supplierNumber = "SUP-503",
                name = "مؤسسة الهلال للمستلزمات الصناعية",
                phone = "778333777",
                companyName = "Al-Hilal Industrial",
                address = "حضرموت - المكلا",
                balanceDue = 430000.0,
                status = "نشط"
            )
            db.supplierDao().insertSupplier(s1)
            db.supplierDao().insertSupplier(s2)
            db.supplierDao().insertSupplier(s3)

            // 6. Items
            val it1 = Item(
                itemCode = "ITM-001",
                name = "منتج فاخر عالي الجودة A1",
                category = "منتجات تامة الصنع",
                unit = "كرتون (24 حبة)",
                unitPrice = 18000.0,
                costPrice = 12500.0,
                currentStock = 450.0,
                minStockAlert = 50.0
            )
            val it2 = Item(
                itemCode = "ITM-002",
                name = "منتج اقتصادي قياسي B2",
                category = "منتجات تامة الصنع",
                unit = "كرتون (12 حبة)",
                unitPrice = 9500.0,
                costPrice = 6800.0,
                currentStock = 820.0,
                minStockAlert = 100.0
            )
            val it3 = Item(
                itemCode = "ITM-003",
                name = "عبوة صناعية ممتازة C3",
                category = "منتجات تعبئة",
                unit = "برميل 50 لتر",
                unitPrice = 42000.0,
                costPrice = 31000.0,
                currentStock = 120.0,
                minStockAlert = 20.0
            )
            db.itemDao().insertItem(it1)
            db.itemDao().insertItem(it2)
            db.itemDao().insertItem(it3)

            // 7. Purchases (Pending Approval by General Manager)
            val po1 = PurchaseOrder(
                orderNumber = "PO-2026-081",
                itemName = "شحنة حبيبات بوليمر أصلية درجة أولى",
                quantity = 15.0,
                totalAmount = 650000.0,
                notes = "مطلوبة لخط الإنتاج الأسبوعي",
                createdByRole = "المدير المالي",
                status = ApprovalStatus.PENDING
            )
            val po2 = PurchaseOrder(
                orderNumber = "PO-2026-082",
                itemName = "كراتين تغليف مطبوعة شعار ليث",
                quantity = 5000.0,
                totalAmount = 280000.0,
                notes = "دفعة تعزيز مخزون التعبئة",
                createdByRole = "المدير المالي",
                status = ApprovalStatus.PENDING
            )
            val po3 = PurchaseOrder(
                orderNumber = "PO-2026-079",
                itemName = "قطع غيار صمامات هيدروليك",
                quantity = 4.0,
                totalAmount = 190000.0,
                notes = "صيانة وقائية لقسم البثق",
                createdByRole = "المدير المالي",
                status = ApprovalStatus.APPROVED,
                approvedDate = now - 86400000L
            )
            db.purchaseDao().insertPurchase(po1)
            db.purchaseDao().insertPurchase(po2)
            db.purchaseDao().insertPurchase(po3)

            // 8. Production Records (Equation: Daily Production - Daily Sales = Remaining)
            val pr1 = ProductionRecord(
                batchNumber = "BATCH-260827-01",
                dateString = "2026-08-27",
                dailyProductionValue = 1500000.0,
                dailySalesValue = 900000.0,
                remainingProductionValue = 600000.0,
                outputQuantity = 150.0,
                notes = "وردية صباحية مكتملة بنجاح مطابق للمواصفات"
            )
            val pr2 = ProductionRecord(
                batchNumber = "BATCH-260826-02",
                dateString = "2026-08-26",
                dailyProductionValue = 1800000.0,
                dailySalesValue = 1100000.0,
                remainingProductionValue = 700000.0,
                outputQuantity = 190.0,
                notes = "إنتاج خطوط التشغيل الرئيسية"
            )
            db.productionDao().insertRecord(pr1)
            db.productionDao().insertRecord(pr2)

            // 9. Raw Materials (Equation: Total Raw Material Value - Issued = Remaining)
            val rm1 = RawMaterial(
                materialCode = "RAW-POL-01",
                name = "حبيبات البوليمر البكر الخام",
                unit = "طن",
                unitPrice = 450000.0,
                totalStockValue = 3600000.0,
                issuedValue = 1200000.0,
                remainingStockValue = 2400000.0,
                currentQuantity = 5.33
            )
            val rm2 = RawMaterial(
                materialCode = "RAW-CLR-02",
                name = "أصباغ ومركزات ملونة صناعية",
                unit = "برميل 25 كجم",
                unitPrice = 85000.0,
                totalStockValue = 950000.0,
                issuedValue = 250000.0,
                remainingStockValue = 700000.0,
                currentQuantity = 8.23
            )
            val rm3 = RawMaterial(
                materialCode = "RAW-BOX-03",
                name = "كرتون مقوى مموج 5 طبقات",
                unit = "رزمة (100 كرتون)",
                unitPrice = 35000.0,
                totalStockValue = 700000.0,
                issuedValue = 150000.0,
                remainingStockValue = 550000.0,
                currentQuantity = 15.7
            )
            db.rawMaterialDao().insertMaterial(rm1)
            db.rawMaterialDao().insertMaterial(rm2)
            db.rawMaterialDao().insertMaterial(rm3)

            // 10. Improvements & Expenses
            val imp1 = ImprovementRecord(
                title = "تحديث فلاتر نظام التبريد الصناعي",
                description = "استبدال فلاتر محطة التبريد المركزي لتحسين كفاءة الطاقة",
                quantity = 6.0,
                estimatedCost = 145000.0,
                status = ApprovalStatus.PENDING,
                requestedBy = "المدير المالي"
            )
            val exp1 = ExpenseRecord(
                expenseCategory = "وقود وكهرباء المصنع",
                description = "فاتورة تزويد ديزل مولدات الطوارئ 1500 لتر",
                amount = 230000.0,
                status = ApprovalStatus.PENDING,
                requestedBy = "المدير المالي"
            )
            val exp2 = ExpenseRecord(
                expenseCategory = "نقل وضيافة",
                description = "مصاريف شحن وتفريغ واردات الميناء",
                amount = 85000.0,
                status = ApprovalStatus.APPROVED,
                requestedBy = "المدير المالي",
                approvedDate = now - 43200000L
            )
            db.improvementDao().insertImprovement(imp1)
            db.expenseDao().insertExpense(exp1)
            db.expenseDao().insertExpense(exp2)

            // 11. User Accounts
            val u1 = UserAccount(
                username = "admin_gm",
                fullName = "الأستاذ ليث (المدير العام)",
                role = AppRole.GENERAL_MANAGER,
                permissions = "ALL_PERMISSIONS,APPROVE_FINANCE,MANAGE_USERS,SYSTEM_ADMIN"
            )
            val u2 = UserAccount(
                username = "finance_dir",
                fullName = "أ. سامي المنصوب (المدير المالي)",
                role = AppRole.FINANCIAL_MANAGER,
                permissions = "COLLECT_DEBTS,DISBURSE_WORKERS,PARTNER_WITHDRAWAL,REQUEST_PURCHASE,RECORD_EXPENSES"
            )
            val u3 = UserAccount(
                username = "sales_lead",
                fullName = "أ. طارق الشميري (مدير المبيعات)",
                role = AppRole.SALES_MANAGER,
                permissions = "CREATE_INVOICE,VIEW_CUSTOMERS,MANAGE_ORDERS"
            )
            val u4 = UserAccount(
                username = "raw_mat_sup",
                fullName = "م. فؤاد العريقي (مسؤول المواد الخام)",
                role = AppRole.RAW_MATERIALS_MANAGER,
                permissions = "MANAGE_RAW_MATERIALS,RECEIVE_RAW,ISSUE_RAW"
            )
            val u5 = UserAccount(
                username = "prod_sup",
                fullName = "م. نبيل الشرجبي (مسؤول الإنتاج)",
                role = AppRole.PRODUCTION_MANAGER,
                permissions = "DAILY_PRODUCTION,PRODUCTION_ORDERS,OUTPUT_LOG"
            )
            db.userDao().insertUser(u1)
            db.userDao().insertUser(u2)
            db.userDao().insertUser(u3)
            db.userDao().insertUser(u4)
            db.userDao().insertUser(u5)

            // 12. App Releases
            val rel1 = AppRelease(
                appName = "Laith ERP Suite",
                versionCode = 105,
                versionName = "v2.5.0-Enterprise",
                releaseNotes = "إضافة معادلات المركز المالي المركزية ودعم الطابعات الحرارية والتدقيق الشامل",
                isMandatory = true
            )
            db.appReleaseDao().insertRelease(rel1)

            // 13. Printers
            val prt1 = PrinterConfig(
                printerName = "طابعة الإدارة الرئيسية (Epson TM-T88VI)",
                address = "192.168.1.150:9100",
                paperSize = "80mm Thermal",
                isDefault = true
            )
            val prt2 = PrinterConfig(
                printerName = "طابعة المبيعات البلوتوث المحمولة",
                address = "BT:00:1A:7D:DA:71:13",
                paperSize = "58mm Portable",
                isDefault = false
            )
            db.printerDao().insertPrinter(prt1)
            db.printerDao().insertPrinter(prt2)

            // 14. Cash Movements Ledger
            val m1 = CashMovement(
                movementNumber = "MOV-1001",
                type = MovementType.DEPOSIT,
                cashboxType = CashboxType.FINANCIAL,
                amount = 450000.0,
                partyName = "شركة الأمل للتجارة والتوزيع",
                partyType = "عميل",
                userRole = "المدير المالي",
                notes = "سند تحصيل دفعة من فاتورة INV-881"
            )
            val m2 = CashMovement(
                movementNumber = "MOV-1002",
                type = MovementType.WITHDRAWAL,
                cashboxType = CashboxType.FINANCIAL,
                amount = 85000.0,
                partyName = "عمر أحمد الجابري",
                partyType = "عامل",
                userRole = "المدير المالي",
                notes = "سحب سلفة مستحقة بخصم من الراتب"
            )
            val m3 = CashMovement(
                movementNumber = "MOV-1003",
                type = MovementType.WITHDRAWAL,
                cashboxType = CashboxType.FINANCIAL,
                amount = 300000.0,
                partyName = "م. خالد سعيد المنصوري",
                partyType = "شريك",
                userRole = "المدير المالي",
                notes = "سحب شهري ضمن السقف المعتمد"
            )
            db.cashMovementDao().insertMovement(m1)
            db.cashMovementDao().insertMovement(m2)
            db.cashMovementDao().insertMovement(m3)

            // 15. Audit Logs
            val a1 = AuditLog(
                userId = 1,
                userName = "الأستاذ ليث",
                userRole = "المدير العام",
                applicationName = "تطبيق المدير العام",
                action = "اعتماد مشتريات",
                entityName = "PurchaseOrder",
                entityId = "PO-2026-079",
                oldValue = "PENDING",
                newValue = "APPROVED (190,000 YER)"
            )
            val a2 = AuditLog(
                userId = 2,
                userName = "أ. سامي المنصوب",
                userRole = "المدير المالي",
                applicationName = "تطبيق المدير المالي",
                action = "تحصيل دفعة عميل",
                entityName = "Customer",
                entityId = "CUST-101",
                oldValue = "Balance: 1,900,000 YER",
                newValue = "Balance: 1,450,000 YER"
            )
            db.auditDao().insertLog(a1)
            db.auditDao().insertLog(a2)

            // 16. Sync Queue Item
            val sq1 = SyncQueueItem(
                entityType = "COLLECTION",
                action = "POST_DEPOSIT",
                payloadJson = "{\"customerId\": 1, \"amount\": 450000, \"cashbox\": \"FINANCIAL\"}",
                status = "SYNCED"
            )
            db.syncDao().insertSyncItem(sq1)
        }
    }
}
