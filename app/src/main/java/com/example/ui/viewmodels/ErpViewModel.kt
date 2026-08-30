package com.example.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.LaithErpDatabase
import com.example.data.models.*
import com.example.data.repository.LaithErpRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

enum class ErpScreen(val titleAr: String, val category: String) {
    DASHBOARD("لوحة التحكم المركزية", "الرئيسية"),
    CENTRAL_REPORT("التقرير المالي الإجمالي", "التقارير"),
    QUERIES("الاستعلامات الشاملة", "البحث"),
    CUSTOMERS("إدارة العملاء والمديونيات", "العملاء"),
    WORKERS("العمال ومسحوبات الرواتب", "الموارد البشرية"),
    PARTNERS("الشركاء والحصص والسحوبات", "الإدارة"),
    SUPPLIERS("الموردون والمستحقات", "المشتريات"),
    ITEMS("الأصناف وقائمة الأسعار", "المخزون"),
    PURCHASES("المشتريات والاعتمادات", "المشتريات"),
    CASHBOXES("الخزائن ودفتر النقدية", "المالية"),
    PRODUCTION_VALUATION("قيمة الإنتاج والمخزون", "الإنتاج"),
    RAW_MATERIALS_VALUATION("قيمة المواد الخام", "المخزون"),
    IMPROVEMENTS("التحسينات والإصلاحات", "العمليات"),
    EXPENSES("المصروفات التشغيلية", "المالية"),
    USERS_ROLES("المستخدمون والأدوار", "النظام"),
    APP_STORE("متجر التطبيقات والإصدارات", "النظام"),
    PRINTERS("الطابعات والفواتير", "النظام"),
    AUDIT_LOGS("سجل التدقيق والمراقبة", "الأمان"),
    OFFLINE_SYNC("المزامنة والاتصال المركزي", "الأمان")
}

data class CentralFinancialSummary(
    val generalCashboxBalance: Double = 0.0,
    val financialCashboxBalance: Double = 0.0,
    val remainingProductionValue: Double = 0.0,
    val remainingRawMaterialsValue: Double = 0.0,
    val totalCustomerReceivables: Double = 0.0,
    val totalSupplierLiabilities: Double = 0.0,
    val totalWorkerLiabilities: Double = 0.0,
    val netTotalWorth: Double = 0.0
)

class ErpViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LaithErpRepository

    init {
        val db = LaithErpDatabase.getDatabase(application, viewModelScope)
        repository = LaithErpRepository(db)
    }

    // Active Application Switcher (👔 General Manager | 💰 Financial Manager | 🧾 Sales | 📦 Raw Materials | 🏭 Production | 🛡️ Auditor)
    private val _currentRole = MutableStateFlow(AppRole.GENERAL_MANAGER)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    private val _currentScreen = MutableStateFlow(ErpScreen.DASHBOARD)
    val currentScreen: StateFlow<ErpScreen> = _currentScreen.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _queryFilterCategory = MutableStateFlow("ALL")
    val queryFilterCategory: StateFlow<String> = _queryFilterCategory.asStateFlow()

    // Snackbar / Feedback Message Channel
    private val _messageChannel = Channel<String>()
    val messageChannel = _messageChannel.receiveAsFlow()

    // Data Flows
    val customers: StateFlow<List<Customer>> = repository.customers.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val workers: StateFlow<List<Worker>> = repository.workers.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val partners: StateFlow<List<Partner>> = repository.partners.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val suppliers: StateFlow<List<Supplier>> = repository.suppliers.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val items: StateFlow<List<Item>> = repository.items.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val purchases: StateFlow<List<PurchaseOrder>> = repository.purchases.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val cashboxes: StateFlow<List<Cashbox>> = repository.cashboxes.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val cashMovements: StateFlow<List<CashMovement>> = repository.cashMovements.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val saleInvoices: StateFlow<List<SaleInvoice>> = repository.saleInvoices.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val productionRecords: StateFlow<List<ProductionRecord>> = repository.productionRecords.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val rawMaterials: StateFlow<List<RawMaterial>> = repository.rawMaterials.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val improvements: StateFlow<List<ImprovementRecord>> = repository.improvements.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val expenses: StateFlow<List<ExpenseRecord>> = repository.expenses.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val users: StateFlow<List<UserAccount>> = repository.users.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val releases: StateFlow<List<AppRelease>> = repository.releases.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val printers: StateFlow<List<PrinterConfig>> = repository.printers.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val auditLogs: StateFlow<List<AuditLog>> = repository.auditLogs.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val syncQueue: StateFlow<List<SyncQueueItem>> = repository.syncQueue.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Central Financial Calculation Formula StateFlow
    private val assetsFlow = combine(
        cashboxes,
        productionRecords,
        rawMaterials,
        customers
    ) { boxes, prods, rawMats, custs ->
        val gmBox = boxes.find { it.type == CashboxType.GENERAL_MANAGER }?.currentBalance ?: 0.0
        val finBox = boxes.find { it.type == CashboxType.FINANCIAL }?.currentBalance ?: 0.0
        val remainingProd = prods.firstOrNull()?.remainingProductionValue ?: 0.0
        val remainingRaw = rawMats.sumOf { it.remainingStockValue }
        val custDebt = custs.sumOf { it.balance }
        listOf(gmBox, finBox, remainingProd, remainingRaw, custDebt)
    }

    private val liabilitiesFlow = combine(
        suppliers,
        workers
    ) { supps, wrks ->
        val suppDue = supps.sumOf { it.balanceDue }
        val wrkAccrued = wrks.sumOf { it.accruedAmount }
        Pair(suppDue, wrkAccrued)
    }

    val centralFinancialSummary: StateFlow<CentralFinancialSummary> = combine(
        assetsFlow,
        liabilitiesFlow
    ) { assets, liabilities ->
        val gmBox = assets[0]
        val finBox = assets[1]
        val remainingProd = assets[2]
        val remainingRaw = assets[3]
        val custDebt = assets[4]
        val (suppDue, wrkAccrued) = liabilities

        // Exact Formula from Rule #43:
        // صافي القيمة = خزينة المدير العام + الخزينة المالية + إنتاج اليوم المتبقي + المواد الخام المتبقية + صافي العملاء - مستحقات الموردين - مستحقات العمال
        val net = (gmBox + finBox + remainingProd + remainingRaw + custDebt) - (suppDue + wrkAccrued)

        CentralFinancialSummary(
            generalCashboxBalance = gmBox,
            financialCashboxBalance = finBox,
            remainingProductionValue = remainingProd,
            remainingRawMaterialsValue = remainingRaw,
            totalCustomerReceivables = custDebt,
            totalSupplierLiabilities = suppDue,
            totalWorkerLiabilities = wrkAccrued,
            netTotalWorth = net
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CentralFinancialSummary())


    // Pending Approvals Count
    val pendingApprovalsCount: StateFlow<Int> = combine(
        purchases, expenses, improvements
    ) { pList, eList, iList ->
        pList.count { it.status == ApprovalStatus.PENDING } +
                eList.count { it.status == ApprovalStatus.PENDING } +
                iList.count { it.status == ApprovalStatus.PENDING }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setRole(role: AppRole) {
        _currentRole.value = role
        // Adjust default screen when role changes
        when (role) {
            AppRole.GENERAL_MANAGER -> _currentScreen.value = ErpScreen.DASHBOARD
            AppRole.FINANCIAL_MANAGER -> _currentScreen.value = ErpScreen.CUSTOMERS
            AppRole.SALES_MANAGER -> _currentScreen.value = ErpScreen.ITEMS
            AppRole.RAW_MATERIALS_MANAGER -> _currentScreen.value = ErpScreen.RAW_MATERIALS_VALUATION
            AppRole.PRODUCTION_MANAGER -> _currentScreen.value = ErpScreen.PRODUCTION_VALUATION
            AppRole.AUDITOR -> _currentScreen.value = ErpScreen.AUDIT_LOGS
        }
    }

    fun setScreen(screen: ErpScreen) {
        _currentScreen.value = screen
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setQueryFilterCategory(cat: String) {
        _queryFilterCategory.value = cat
    }

    // --- Actions ---

    fun collectCustomerDebt(customerId: Long, amount: Double, notes: String) {
        viewModelScope.launch {
            val result = repository.collectCustomerDebt(
                customerId = customerId,
                amount = amount,
                userRole = _currentRole.value.titleAr,
                userName = _currentRole.value.titleAr,
                notes = notes
            )
            result.fold(
                onSuccess = { msg -> _messageChannel.send(msg) },
                onFailure = { err -> _messageChannel.send("خطأ: ${err.message}") }
            )
        }
    }

    fun withdrawWorkerAmount(workerId: Long, amount: Double, notes: String) {
        viewModelScope.launch {
            val result = repository.withdrawWorkerAmount(
                workerId = workerId,
                amount = amount,
                userRole = _currentRole.value.titleAr,
                userName = _currentRole.value.titleAr,
                notes = notes
            )
            result.fold(
                onSuccess = { msg -> _messageChannel.send(msg) },
                onFailure = { err -> _messageChannel.send("خطأ: ${err.message}") }
            )
        }
    }

    fun withdrawPartnerAmount(partnerId: Long, amount: Double, notes: String) {
        viewModelScope.launch {
            val result = repository.withdrawPartnerAmount(
                partnerId = partnerId,
                amount = amount,
                userRole = _currentRole.value.titleAr,
                userName = _currentRole.value.titleAr,
                notes = notes
            )
            result.fold(
                onSuccess = { msg -> _messageChannel.send(msg) },
                onFailure = { err -> _messageChannel.send("خطأ: ${err.message}") }
            )
        }
    }

    fun paySupplier(supplierId: Long, amount: Double, notes: String) {
        viewModelScope.launch {
            val result = repository.paySupplierAmount(
                supplierId = supplierId,
                amount = amount,
                userRole = _currentRole.value.titleAr,
                userName = _currentRole.value.titleAr,
                notes = notes
            )
            result.fold(
                onSuccess = { msg -> _messageChannel.send(msg) },
                onFailure = { err -> _messageChannel.send("خطأ: ${err.message}") }
            )
        }
    }

    fun createPurchaseRequest(itemName: String, quantity: Double, totalAmount: Double, notes: String) {
        viewModelScope.launch {
            repository.createPurchaseRequest(itemName, quantity, totalAmount, notes)
            _messageChannel.send("تم إرسال طلب الشراء إلى المدير العام بنجاح بانتظار الاعتماد.")
        }
    }

    fun approvePurchase(purchase: PurchaseOrder) {
        viewModelScope.launch {
            val res = repository.approvePurchaseOrder(purchase, _currentRole.value.titleAr)
            res.fold(
                onSuccess = { msg -> _messageChannel.send(msg) },
                onFailure = { err -> _messageChannel.send("خطأ: ${err.message}") }
            )
        }
    }

    fun rejectPurchase(purchase: PurchaseOrder) {
        viewModelScope.launch {
            val res = repository.rejectPurchaseOrder(purchase, _currentRole.value.titleAr)
            res.fold(
                onSuccess = { msg -> _messageChannel.send(msg) },
                onFailure = { err -> _messageChannel.send("خطأ: ${err.message}") }
            )
        }
    }

    fun createExpenseRequest(category: String, description: String, amount: Double) {
        viewModelScope.launch {
            repository.createExpenseRequest(category, description, amount)
            _messageChannel.send("تم رفع طلب المصروف للمدير العام بانتظار الاعتماد.")
        }
    }

    fun approveExpense(expense: ExpenseRecord) {
        viewModelScope.launch {
            val res = repository.approveExpense(expense, _currentRole.value.titleAr)
            res.fold(
                onSuccess = { msg -> _messageChannel.send(msg) },
                onFailure = { err -> _messageChannel.send("خطأ: ${err.message}") }
            )
        }
    }

    fun approveImprovement(improvement: ImprovementRecord) {
        viewModelScope.launch {
            val res = repository.approveImprovement(improvement, _currentRole.value.titleAr)
            res.fold(
                onSuccess = { msg -> _messageChannel.send(msg) },
                onFailure = { err -> _messageChannel.send("خطأ: ${err.message}") }
            )
        }
    }

    fun recordDailyProduction(batch: String, prodVal: Double, salesVal: Double, qty: Double, notes: String) {
        viewModelScope.launch {
            repository.recordDailyProduction(batch, prodVal, salesVal, qty, notes)
            _messageChannel.send("تم حفظ معادلة الإنتاج: المتبقي = ${prodVal - salesVal} YER")
        }
    }

    fun updateRawMaterial(id: Long, name: String, unit: String, unitPrice: Double, totalVal: Double, issuedVal: Double, qty: Double) {
        viewModelScope.launch {
            repository.updateRawMaterialMovement(id, name, unit, unitPrice, totalVal, issuedVal, qty)
            _messageChannel.send("تم حفظ حركة المادة الخام: المتبقي = ${totalVal - issuedVal} YER")
        }
    }

    fun createSaleInvoice(customerId: Long, total: Double, paid: Double, summary: String) {
        viewModelScope.launch {
            val res = repository.createSaleInvoice(customerId, total, paid, summary)
            res.fold(
                onSuccess = { msg -> _messageChannel.send(msg) },
                onFailure = { err -> _messageChannel.send("خطأ: ${err.message}") }
            )
        }
    }

    fun transferCashbox(from: CashboxType, to: CashboxType, amount: Double, notes: String) {
        viewModelScope.launch {
            val res = repository.transferBetweenCashboxes(from, to, amount, _currentRole.value.titleAr, notes)
            res.fold(
                onSuccess = { msg -> _messageChannel.send(msg) },
                onFailure = { err -> _messageChannel.send("خطأ: ${err.message}") }
            )
        }
    }

    fun addCustomer(name: String, phone: String, address: String, creditLimit: Double, initialDebt: Double) {
        viewModelScope.launch {
            val cust = Customer(
                customerNumber = "CUST-${(100..999).random()}",
                name = name,
                phone = phone,
                address = address,
                creditLimit = creditLimit,
                balance = initialDebt
            )
            repository.insertCustomer(cust)
            _messageChannel.send("تمت إضافة العميل $name بنجاح.")
        }
    }

    fun addWorker(name: String, phone: String, jobTitle: String, basicSalary: Double, accrued: Double) {
        viewModelScope.launch {
            val wrk = Worker(
                workerNumber = "WRK-${(100..999).random()}",
                name = name,
                phone = phone,
                jobTitle = jobTitle,
                basicSalary = basicSalary,
                accruedAmount = accrued,
                totalWithdrawn = 0.0
            )
            repository.insertWorker(wrk)
            _messageChannel.send("تمت إضافة العامل $name بنجاح.")
        }
    }

    fun addPartner(name: String, phone: String, ratio: Double, limit: Double) {
        viewModelScope.launch {
            val prt = Partner(
                partnerNumber = "PRT-${(10..99).random()}",
                name = name,
                phone = phone,
                contributionRatio = ratio,
                monthlyWithdrawalLimit = limit
            )
            repository.insertPartner(prt)
            _messageChannel.send("تمت إضافة الشريك $name بنجاح.")
        }
    }

    fun addSupplier(name: String, company: String, phone: String, address: String, initialDue: Double) {
        viewModelScope.launch {
            val sup = Supplier(
                supplierNumber = "SUP-${(100..999).random()}",
                name = name,
                companyName = company,
                phone = phone,
                address = address,
                balanceDue = initialDue
            )
            repository.insertSupplier(sup)
            _messageChannel.send("تمت إضافة المورد $company بنجاح.")
        }
    }

    fun addItem(name: String, code: String, category: String, unit: String, price: Double, cost: Double, stock: Double) {
        viewModelScope.launch {
            val itm = Item(
                itemCode = code.ifBlank { "ITM-${(100..999).random()}" },
                name = name,
                category = category,
                unit = unit,
                unitPrice = price,
                costPrice = cost,
                currentStock = stock
            )
            repository.insertItem(itm)
            _messageChannel.send("تمت إضافة الصنف $name بنجاح.")
        }
    }

    fun addUser(fullName: String, username: String, role: AppRole, permissions: String) {
        viewModelScope.launch {
            val usr = UserAccount(
                fullName = fullName,
                username = username,
                role = role,
                permissions = permissions
            )
            repository.insertUser(usr)
            _messageChannel.send("تمت إضافة المستخدم $fullName بصلاحية ${role.titleAr}.")
        }
    }

    fun addPrinter(name: String, address: String, paperSize: String) {
        viewModelScope.launch {
            val prt = PrinterConfig(
                printerName = name,
                address = address,
                paperSize = paperSize
            )
            repository.insertPrinter(prt)
            _messageChannel.send("تم حفظ إعدادات الطابعة $name.")
        }
    }

    fun triggerManualSync() {
        viewModelScope.launch {
            _messageChannel.send("جاري الاتصال بـ Laith ERP Central Server (Asia/Aden UTC+3)...")
            kotlinx.coroutines.delay(1200)
            repository.clearSyncedQueue()
            _messageChannel.send("تمت مزامنة جميع السجلات والعمليات المحلية بنجاح مع الخادم المركزي.")
        }
    }

    companion object {
        fun formatCurrency(amount: Double): String {
            val formatter = NumberFormat.getNumberInstance(Locale("ar", "YE"))
            formatter.maximumFractionDigits = 0
            return "${formatter.format(amount)} ر.ي"
        }
    }
}
