package com.example.data.repository

import com.example.data.db.LaithErpDatabase
import com.example.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LaithErpRepository(private val db: LaithErpDatabase) {

    // Flows
    val customers: Flow<List<Customer>> = db.customerDao().getAllCustomers()
    val workers: Flow<List<Worker>> = db.workerDao().getAllWorkers()
    val partners: Flow<List<Partner>> = db.partnerDao().getAllPartners()
    val suppliers: Flow<List<Supplier>> = db.supplierDao().getAllSuppliers()
    val items: Flow<List<Item>> = db.itemDao().getAllItems()
    val purchases: Flow<List<PurchaseOrder>> = db.purchaseDao().getAllPurchases()
    val cashboxes: Flow<List<Cashbox>> = db.cashboxDao().getAllCashboxes()
    val cashMovements: Flow<List<CashMovement>> = db.cashMovementDao().getAllMovements()
    val saleInvoices: Flow<List<SaleInvoice>> = db.saleInvoiceDao().getAllInvoices()
    val productionRecords: Flow<List<ProductionRecord>> = db.productionDao().getAllProductionRecords()
    val rawMaterials: Flow<List<RawMaterial>> = db.rawMaterialDao().getAllMaterials()
    val improvements: Flow<List<ImprovementRecord>> = db.improvementDao().getAllImprovements()
    val expenses: Flow<List<ExpenseRecord>> = db.expenseDao().getAllExpenses()
    val users: Flow<List<UserAccount>> = db.userDao().getAllUsers()
    val releases: Flow<List<AppRelease>> = db.appReleaseDao().getAllReleases()
    val printers: Flow<List<PrinterConfig>> = db.printerDao().getAllPrinters()
    val auditLogs: Flow<List<AuditLog>> = db.auditDao().getAllLogs()
    val syncQueue: Flow<List<SyncQueueItem>> = db.syncDao().getAllSyncItems()

    // 1. Customer Collection (تحصيل مالي)
    suspend fun collectCustomerDebt(
        customerId: Long,
        amount: Double,
        userRole: String,
        userName: String,
        notes: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val customer = db.customerDao().getCustomerById(customerId)
            ?: return@withContext Result.failure(Exception("العميل غير موجود"))

        if (amount <= 0) {
            return@withContext Result.failure(Exception("المبلغ المحصل يجب أن يكون أكبر من الصفر"))
        }

        val now = System.currentTimeMillis()
        // Reduce customer balance
        db.customerDao().reduceDebt(customerId, amount)
        // Deposit into Financial Cashbox
        db.cashboxDao().deposit(CashboxType.FINANCIAL, amount, now)

        // Record Cash Movement
        val movNumber = "MOV-${System.currentTimeMillis() % 100000}"
        db.cashMovementDao().insertMovement(
            CashMovement(
                movementNumber = movNumber,
                type = MovementType.DEPOSIT,
                cashboxType = CashboxType.FINANCIAL,
                amount = amount,
                partyName = customer.name,
                partyType = "عميل",
                userRole = userRole,
                notes = if (notes.isBlank()) "تحصيل دفعة مالية من العميل" else notes,
                timestamp = now
            )
        )

        // Record Audit Log
        db.auditDao().insertLog(
            AuditLog(
                userId = 2,
                userName = userName,
                userRole = userRole,
                applicationName = "تطبيق المدير المالي",
                action = "تحصيل دفعة عميل",
                entityName = "Customer",
                entityId = customer.customerNumber,
                oldValue = "الرصيد السابق: ${customer.balance}",
                newValue = "الرصيد الجديد: ${customer.balance - amount} | المبلغ المحصل: $amount",
                timestamp = now
            )
        )

        // Add to Sync Queue
        db.syncDao().insertSyncItem(
            SyncQueueItem(
                entityType = "COLLECTION",
                action = "CUSTOMER_PAYMENT",
                payloadJson = "{\"customerId\": $customerId, \"amount\": $amount, \"time\": $now}"
            )
        )

        Result.success("تم تحصيل مبلغ $amount بنجاح وتوريده إلى الخزينة المالية وتخفيض مديونية العميل.")
    }

    // 2. Worker Withdrawal (سحب عمال مع فحص المستحق)
    suspend fun withdrawWorkerAmount(
        workerId: Long,
        amount: Double,
        userRole: String,
        userName: String,
        notes: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val worker = db.workerDao().getWorkerById(workerId)
            ?: return@withContext Result.failure(Exception("العامل غير موجود"))

        if (amount <= 0) {
            return@withContext Result.failure(Exception("مبلغ السحب يجب أن يكون أكبر من الصفر"))
        }

        if (amount > worker.accruedAmount) {
            return@withContext Result.failure(
                Exception("تنبيه نظامي: المبلغ المطلوب ($amount) يتجاوز رصيد الاستحقاق الحالي (${worker.accruedAmount})!")
            )
        }

        val financialCashbox = db.cashboxDao().getCashboxByType(CashboxType.FINANCIAL)
        if (financialCashbox != null && financialCashbox.currentBalance < amount) {
            return@withContext Result.failure(
                Exception("رصيد الخزينة المالية (${financialCashbox.currentBalance}) غير كافٍ لصرف هذا السحب!")
            )
        }

        val now = System.currentTimeMillis()
        // Record withdrawal in worker account
        db.workerDao().recordWithdrawal(workerId, amount)
        // Deduct from Financial Cashbox
        db.cashboxDao().withdraw(CashboxType.FINANCIAL, amount, now)

        val movNumber = "MOV-${System.currentTimeMillis() % 100000}"
        db.cashMovementDao().insertMovement(
            CashMovement(
                movementNumber = movNumber,
                type = MovementType.WITHDRAWAL,
                cashboxType = CashboxType.FINANCIAL,
                amount = amount,
                partyName = worker.name,
                partyType = "عامل",
                userRole = userRole,
                notes = if (notes.isBlank()) "سحب سلفة/مستحقات للعامل" else notes,
                timestamp = now
            )
        )

        db.auditDao().insertLog(
            AuditLog(
                userId = 2,
                userName = userName,
                userRole = userRole,
                applicationName = "تطبيق المدير المالي",
                action = "صرف سحب عامل",
                entityName = "Worker",
                entityId = worker.workerNumber,
                oldValue = "المستحق السابق: ${worker.accruedAmount}",
                newValue = "المستحق المتبقي: ${worker.accruedAmount - amount}",
                timestamp = now
            )
        )

        Result.success("تم صرف سحب العامل بمبلغ $amount بنجاح وتحديث حسابه ودفتر الخزينة.")
    }

    // 3. Partner Monthly Withdrawal (سحب الشركاء مع فحص السقف الشهري)
    suspend fun withdrawPartnerAmount(
        partnerId: Long,
        amount: Double,
        userRole: String,
        userName: String,
        notes: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val partner = db.partnerDao().getPartnerById(partnerId)
            ?: return@withContext Result.failure(Exception("الشريك غير موجود"))

        if (amount <= 0) {
            return@withContext Result.failure(Exception("مبلغ السحب يجب أن يكون أكبر من الصفر"))
        }

        val totalAfterWithdrawal = partner.currentMonthWithdrawn + amount
        if (totalAfterWithdrawal > partner.monthlyWithdrawalLimit) {
            val remainingLimit = partner.monthlyWithdrawalLimit - partner.currentMonthWithdrawn
            return@withContext Result.failure(
                Exception("تجاوز السقف الشهري! السقف: ${partner.monthlyWithdrawalLimit}، المسحوب: ${partner.currentMonthWithdrawn}، المتبقي المتاح: $remainingLimit")
            )
        }

        val financialCashbox = db.cashboxDao().getCashboxByType(CashboxType.FINANCIAL)
        if (financialCashbox != null && financialCashbox.currentBalance < amount) {
            return@withContext Result.failure(
                Exception("رصيد الخزينة المالية غير كافٍ لصرف هذا السحب للشريك!")
            )
        }

        val now = System.currentTimeMillis()
        db.partnerDao().recordPartnerWithdrawal(partnerId, amount)
        db.cashboxDao().withdraw(CashboxType.FINANCIAL, amount, now)

        val movNumber = "MOV-${System.currentTimeMillis() % 100000}"
        db.cashMovementDao().insertMovement(
            CashMovement(
                movementNumber = movNumber,
                type = MovementType.WITHDRAWAL,
                cashboxType = CashboxType.FINANCIAL,
                amount = amount,
                partyName = partner.name,
                partyType = "شريك",
                userRole = userRole,
                notes = if (notes.isBlank()) "مسحوبات شهرية للشريك" else notes,
                timestamp = now
            )
        )

        db.auditDao().insertLog(
            AuditLog(
                userId = 2,
                userName = userName,
                userRole = userRole,
                applicationName = "تطبيق المدير المالي",
                action = "سحب شريك شهري",
                entityName = "Partner",
                entityId = partner.partnerNumber,
                oldValue = "المسحوب السابق: ${partner.currentMonthWithdrawn}",
                newValue = "إجمالي المسحوب: $totalAfterWithdrawal من أصل ${partner.monthlyWithdrawalLimit}",
                timestamp = now
            )
        )

        Result.success("تم صرف مبلغ $amount للشريك بنجاح ومطابق للحد الشهري المعتمد.")
    }

    // 4. Supplier Payment (سداد مستحقات مورد)
    suspend fun paySupplierAmount(
        supplierId: Long,
        amount: Double,
        userRole: String,
        userName: String,
        notes: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val supplier = db.supplierDao().getSupplierById(supplierId)
            ?: return@withContext Result.failure(Exception("المورد غير موجود"))

        if (amount <= 0) {
            return@withContext Result.failure(Exception("مبلغ السداد يجب أن يكون أكبر من الصفر"))
        }

        val now = System.currentTimeMillis()
        db.supplierDao().paySupplier(supplierId, amount)
        db.cashboxDao().withdraw(CashboxType.FINANCIAL, amount, now)

        val movNumber = "MOV-${System.currentTimeMillis() % 100000}"
        db.cashMovementDao().insertMovement(
            CashMovement(
                movementNumber = movNumber,
                type = MovementType.WITHDRAWAL,
                cashboxType = CashboxType.FINANCIAL,
                amount = amount,
                partyName = supplier.companyName,
                partyType = "مورد",
                userRole = userRole,
                notes = if (notes.isBlank()) "سداد دفعة من مستحقات المورد" else notes,
                timestamp = now
            )
        )

        db.auditDao().insertLog(
            AuditLog(
                userId = 2,
                userName = userName,
                userRole = userRole,
                applicationName = "تطبيق المدير المالي",
                action = "سداد مستحقات مورد",
                entityName = "Supplier",
                entityId = supplier.supplierNumber,
                oldValue = "المستحق السابق: ${supplier.balanceDue}",
                newValue = "المستحق المتبقي: ${supplier.balanceDue - amount}",
                timestamp = now
            )
        )

        Result.success("تم سداد مبلغ $amount للمورد بنجاح وتخفيض الالتزام.")
    }

    // 5. Purchases Workflow (Finance creates -> General Manager approves)
    suspend fun createPurchaseRequest(
        itemName: String,
        quantity: Double,
        totalAmount: Double,
        notes: String
    ): Long = withContext(Dispatchers.IO) {
        val order = PurchaseOrder(
            orderNumber = "PO-2026-${(100..999).random()}",
            itemName = itemName,
            quantity = quantity,
            totalAmount = totalAmount,
            notes = notes,
            createdByRole = "المدير المالي",
            status = ApprovalStatus.PENDING
        )
        val id = db.purchaseDao().insertPurchase(order)
        db.auditDao().insertLog(
            AuditLog(
                userId = 2,
                userName = "المدير المالي",
                userRole = "المدير المالي",
                applicationName = "تطبيق المدير المالي",
                action = "إنشاء طلب مشتريات بانتظار الاعتماد",
                entityName = "PurchaseOrder",
                entityId = order.orderNumber,
                newValue = "$itemName | كمية: $quantity | قيمة: $totalAmount",
                timestamp = System.currentTimeMillis()
            )
        )
        id
    }

    suspend fun approvePurchaseOrder(
        purchase: PurchaseOrder,
        approverName: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        // Deduct from Financial Cashbox
        db.cashboxDao().withdraw(CashboxType.FINANCIAL, purchase.totalAmount, now)
        db.purchaseDao().updateStatus(purchase.id, ApprovalStatus.APPROVED, now)

        val movNumber = "MOV-${System.currentTimeMillis() % 100000}"
        db.cashMovementDao().insertMovement(
            CashMovement(
                movementNumber = movNumber,
                type = MovementType.PURCHASE,
                cashboxType = CashboxType.FINANCIAL,
                amount = purchase.totalAmount,
                partyName = purchase.itemName,
                partyType = "مشتريات معتمدة",
                userRole = "المدير العام",
                notes = "اعتماد مشتريات طلب رقم ${purchase.orderNumber}",
                timestamp = now
            )
        )

        db.auditDao().insertLog(
            AuditLog(
                userId = 1,
                userName = approverName,
                userRole = "المدير العام",
                applicationName = "تطبيق المدير العام",
                action = "اعتماد مشتريات وخصم مالي",
                entityName = "PurchaseOrder",
                entityId = purchase.orderNumber,
                oldValue = "PENDING",
                newValue = "APPROVED - خصم ${purchase.totalAmount} من الخزينة",
                timestamp = now
            )
        )

        Result.success("تم اعتماد طلب الشراء رقم ${purchase.orderNumber} وخصم المبلغ من الخزينة المالية.")
    }

    suspend fun rejectPurchaseOrder(
        purchase: PurchaseOrder,
        approverName: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        db.purchaseDao().updateStatus(purchase.id, ApprovalStatus.REJECTED, now)
        db.auditDao().insertLog(
            AuditLog(
                userId = 1,
                userName = approverName,
                userRole = "المدير العام",
                applicationName = "تطبيق المدير العام",
                action = "رفض طلب مشتريات",
                entityName = "PurchaseOrder",
                entityId = purchase.orderNumber,
                oldValue = "PENDING",
                newValue = "REJECTED",
                timestamp = now
            )
        )
        Result.success("تم رفض طلب الشراء رقم ${purchase.orderNumber}.")
    }

    // 6. Expense & Improvement Approvals (Finance requests -> GM approves)
    suspend fun createExpenseRequest(
        category: String,
        description: String,
        amount: Double
    ): Long = withContext(Dispatchers.IO) {
        val exp = ExpenseRecord(
            expenseCategory = category,
            description = description,
            amount = amount,
            status = ApprovalStatus.PENDING
        )
        db.expenseDao().insertExpense(exp)
    }

    suspend fun approveExpense(
        expense: ExpenseRecord,
        approverName: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        db.cashboxDao().withdraw(CashboxType.FINANCIAL, expense.amount, now)
        db.expenseDao().updateStatus(expense.id, ApprovalStatus.APPROVED, now)

        val movNumber = "MOV-${System.currentTimeMillis() % 100000}"
        db.cashMovementDao().insertMovement(
            CashMovement(
                movementNumber = movNumber,
                type = MovementType.EXPENSE,
                cashboxType = CashboxType.FINANCIAL,
                amount = expense.amount,
                partyName = expense.expenseCategory,
                partyType = "مصروف تشغيلي",
                userRole = "المدير العام",
                notes = expense.description,
                timestamp = now
            )
        )

        db.auditDao().insertLog(
            AuditLog(
                userId = 1,
                userName = approverName,
                userRole = "المدير العام",
                applicationName = "تطبيق المدير العام",
                action = "اعتماد مصروف وخصم من الخزينة",
                entityName = "Expense",
                entityId = "EXP-${expense.id}",
                newValue = "APPROVED: ${expense.amount} YER",
                timestamp = now
            )
        )

        Result.success("تم اعتماد المصروف بنجاح وخصم ${expense.amount} من الخزينة المالية.")
    }

    suspend fun approveImprovement(
        improvement: ImprovementRecord,
        approverName: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        db.cashboxDao().withdraw(CashboxType.FINANCIAL, improvement.estimatedCost, now)
        db.improvementDao().updateStatus(improvement.id, ApprovalStatus.APPROVED, now)

        val movNumber = "MOV-${System.currentTimeMillis() % 100000}"
        db.cashMovementDao().insertMovement(
            CashMovement(
                movementNumber = movNumber,
                type = MovementType.EXPENSE,
                cashboxType = CashboxType.FINANCIAL,
                amount = improvement.estimatedCost,
                partyName = improvement.title,
                partyType = "تحسينات وإصلاحات",
                userRole = "المدير العام",
                notes = improvement.description,
                timestamp = now
            )
        )

        db.auditDao().insertLog(
            AuditLog(
                userId = 1,
                userName = approverName,
                userRole = "المدير العام",
                applicationName = "تطبيق المدير العام",
                action = "اعتماد تحسينات وإصلاحات",
                entityName = "Improvement",
                entityId = "IMP-${improvement.id}",
                newValue = "APPROVED: ${improvement.estimatedCost} YER",
                timestamp = now
            )
        )

        Result.success("تم اعتماد عملية التحسين والإصلاح وخصم ${improvement.estimatedCost} من الخزينة.")
    }

    // 7. Daily Production Record (معادلة قيمة إنتاج اليوم - قيمة مبيعات اليوم = المتبقي)
    suspend fun recordDailyProduction(
        batchNumber: String,
        dailyProductionValue: Double,
        dailySalesValue: Double,
        outputQuantity: Double,
        notes: String
    ): Long = withContext(Dispatchers.IO) {
        val remaining = dailyProductionValue - dailySalesValue
        val now = System.currentTimeMillis()
        val record = ProductionRecord(
            batchNumber = batchNumber,
            dateString = "2026-08-27",
            dailyProductionValue = dailyProductionValue,
            dailySalesValue = dailySalesValue,
            remainingProductionValue = remaining,
            outputQuantity = outputQuantity,
            notes = notes,
            timestamp = now
        )
        val id = db.productionDao().insertRecord(record)
        db.auditDao().insertLog(
            AuditLog(
                userId = 5,
                userName = "مسؤول الإنتاج",
                userRole = "مسؤول الإنتاج",
                applicationName = "تطبيق الإنتاج",
                action = "تسجيل إنتاج يومي وحساب المتبقي",
                entityName = "ProductionRecord",
                entityId = batchNumber,
                newValue = "إنتاج: $dailyProductionValue | مبيعات: $dailySalesValue | متبقي: $remaining",
                timestamp = now
            )
        )
        id
    }

    // 8. Raw Materials Equation & Movements (المعادلة: قيمة المواد الخام - الصادر = المتبقي)
    suspend fun updateRawMaterialMovement(
        materialId: Long,
        name: String,
        unit: String,
        unitPrice: Double,
        totalStockValue: Double,
        issuedValue: Double,
        currentQuantity: Double
    ) = withContext(Dispatchers.IO) {
        val remainingValue = totalStockValue - issuedValue
        val mat = RawMaterial(
            id = materialId,
            materialCode = "RAW-${(100..999).random()}",
            name = name,
            unit = unit,
            unitPrice = unitPrice,
            totalStockValue = totalStockValue,
            issuedValue = issuedValue,
            remainingStockValue = remainingValue,
            currentQuantity = currentQuantity
        )
        if (materialId == 0L) {
            db.rawMaterialDao().insertMaterial(mat)
        } else {
            db.rawMaterialDao().updateMaterial(mat)
        }
        db.auditDao().insertLog(
            AuditLog(
                userId = 4,
                userName = "مسؤول المواد الخام",
                userRole = "مسؤول المواد الخام",
                applicationName = "تطبيق المواد الخام",
                action = "تحديث رصيد ومعادلة مادة خام",
                entityName = "RawMaterial",
                entityId = mat.materialCode,
                newValue = "إجمالي: $totalStockValue | صادر: $issuedValue | متبقي: $remainingValue",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // 9. Sales Invoice (تطبيق المبيعات)
    suspend fun createSaleInvoice(
        customerId: Long,
        totalAmount: Double,
        paidAmount: Double,
        itemsSummary: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val customer = db.customerDao().getCustomerById(customerId)
            ?: return@withContext Result.failure(Exception("العميل غير موجود"))

        val remainingDebt = totalAmount - paidAmount
        val now = System.currentTimeMillis()
        val invoiceNumber = "INV-2026-${(1000..9999).random()}"

        val invoice = SaleInvoice(
            invoiceNumber = invoiceNumber,
            customerId = customerId,
            customerName = customer.name,
            totalAmount = totalAmount,
            paidAmount = paidAmount,
            remainingDebt = remainingDebt,
            itemsSummary = itemsSummary,
            timestamp = now
        )
        db.saleInvoiceDao().insertInvoice(invoice)

        // Add remaining debt to customer balance
        if (remainingDebt > 0) {
            db.customerDao().addDebt(customerId, remainingDebt)
        }

        // If paid amount > 0, deposit into Financial Cashbox
        if (paidAmount > 0) {
            db.cashboxDao().deposit(CashboxType.FINANCIAL, paidAmount, now)
            val movNumber = "MOV-${System.currentTimeMillis() % 100000}"
            db.cashMovementDao().insertMovement(
                CashMovement(
                    movementNumber = movNumber,
                    type = MovementType.DEPOSIT,
                    cashboxType = CashboxType.FINANCIAL,
                    amount = paidAmount,
                    partyName = customer.name,
                    partyType = "عميل (فاتورة مبيعات)",
                    userRole = "مدير المبيعات",
                    notes = "دفعة مسددة فورية من فاتورة $invoiceNumber",
                    timestamp = now
                )
            )
        }

        db.auditDao().insertLog(
            AuditLog(
                userId = 3,
                userName = "مدير المبيعات",
                userRole = "مدير المبيعات",
                applicationName = "تطبيق المبيعات",
                action = "إنشاء فاتورة مبيعات",
                entityName = "SaleInvoice",
                entityId = invoiceNumber,
                newValue = "إجمالي: $totalAmount | مدفوع: $paidAmount | مديونية مضافة: $remainingDebt",
                timestamp = now
            )
        )

        Result.success("تم إصدار الفاتورة رقم $invoiceNumber بنجاح وإضافة المديونية المتبقية لحساب العميل.")
    }

    // 10. Cashbox Transfer (تحويل بين الخزائن)
    suspend fun transferBetweenCashboxes(
        fromType: CashboxType,
        toType: CashboxType,
        amount: Double,
        userName: String,
        notes: String
    ): Result<String> = withContext(Dispatchers.IO) {
        if (amount <= 0) {
            return@withContext Result.failure(Exception("مبلغ التحويل يجب أن يكون أكبر من الصفر"))
        }
        val sourceBox = db.cashboxDao().getCashboxByType(fromType)
            ?: return@withContext Result.failure(Exception("الخزينة المصدر غير موجودة"))

        if (sourceBox.currentBalance < amount) {
            return@withContext Result.failure(Exception("رصيد الخزينة المصدر (${sourceBox.currentBalance}) غير كافٍ للتحويل"))
        }

        val now = System.currentTimeMillis()
        db.cashboxDao().withdraw(fromType, amount, now)
        db.cashboxDao().deposit(toType, amount, now)

        val movNumber = "MOV-${System.currentTimeMillis() % 100000}"
        db.cashMovementDao().insertMovement(
            CashMovement(
                movementNumber = movNumber,
                type = MovementType.TRANSFER,
                cashboxType = fromType,
                amount = amount,
                partyName = toType.titleAr,
                partyType = "تحويل داخلي",
                userRole = "المدير العام",
                notes = if (notes.isBlank()) "تحويل نقدية بين الخزائن" else notes,
                timestamp = now
            )
        )

        db.auditDao().insertLog(
            AuditLog(
                userId = 1,
                userName = userName,
                userRole = "المدير العام",
                applicationName = "تطبيق المدير العام",
                action = "تحويل نقدية بين الخزائن",
                entityName = "Cashbox",
                entityId = "${fromType.name} -> ${toType.name}",
                newValue = "مبلغ التحويل: $amount YER",
                timestamp = now
            )
        )

        Result.success("تم تحويل $amount YER من ${fromType.titleAr} إلى ${toType.titleAr} بنجاح.")
    }

    // Simple inserts for Customers, Workers, Partners, Suppliers, Items, Users
    suspend fun insertCustomer(customer: Customer) = withContext(Dispatchers.IO) {
        db.customerDao().insertCustomer(customer)
    }

    suspend fun insertWorker(worker: Worker) = withContext(Dispatchers.IO) {
        db.workerDao().insertWorker(worker)
    }

    suspend fun insertPartner(partner: Partner) = withContext(Dispatchers.IO) {
        db.partnerDao().insertPartner(partner)
    }

    suspend fun insertSupplier(supplier: Supplier) = withContext(Dispatchers.IO) {
        db.supplierDao().insertSupplier(supplier)
    }

    suspend fun insertItem(item: Item) = withContext(Dispatchers.IO) {
        db.itemDao().insertItem(item)
    }

    suspend fun insertUser(user: UserAccount) = withContext(Dispatchers.IO) {
        db.userDao().insertUser(user)
    }

    suspend fun insertPrinter(printer: PrinterConfig) = withContext(Dispatchers.IO) {
        db.printerDao().insertPrinter(printer)
    }

    suspend fun clearSyncedQueue() = withContext(Dispatchers.IO) {
        db.syncDao().clearSynced()
    }
}
