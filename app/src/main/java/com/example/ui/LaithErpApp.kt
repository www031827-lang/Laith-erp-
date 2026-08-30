package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.models.AppRole
import com.example.ui.components.ErpNavigationDrawerContent
import com.example.ui.components.ErpTopAppBar
import com.example.ui.screens.*
import com.example.ui.viewmodels.ErpScreen
import com.example.ui.viewmodels.ErpViewModel
import kotlinx.coroutines.launch

@Composable
fun LaithErpApp(
    viewModel: ErpViewModel = viewModel()
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val categoryFilter by viewModel.queryFilterCategory.collectAsStateWithLifecycle()

    val centralSummary by viewModel.centralFinancialSummary.collectAsStateWithLifecycle()
    val pendingApprovalsCount by viewModel.pendingApprovalsCount.collectAsStateWithLifecycle()

    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val workers by viewModel.workers.collectAsStateWithLifecycle()
    val partners by viewModel.partners.collectAsStateWithLifecycle()
    val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()
    val items by viewModel.items.collectAsStateWithLifecycle()
    val purchases by viewModel.purchases.collectAsStateWithLifecycle()
    val cashboxes by viewModel.cashboxes.collectAsStateWithLifecycle()
    val cashMovements by viewModel.cashMovements.collectAsStateWithLifecycle()
    val productionRecords by viewModel.productionRecords.collectAsStateWithLifecycle()
    val rawMaterials by viewModel.rawMaterials.collectAsStateWithLifecycle()
    val improvements by viewModel.improvements.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()
    val releases by viewModel.releases.collectAsStateWithLifecycle()
    val printers by viewModel.printers.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val syncQueue by viewModel.syncQueue.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Listen to ViewModel message channel
    LaunchedEffect(Unit) {
        viewModel.messageChannel.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ErpNavigationDrawerContent(
                    currentScreen = currentScreen,
                    onScreenSelect = { screen ->
                        viewModel.setScreen(screen)
                    },
                    pendingApprovalsCount = pendingApprovalsCount,
                    onDismiss = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    ErpTopAppBar(
                        currentRole = currentRole,
                        currentScreen = currentScreen,
                        onRoleSelect = { role ->
                            viewModel.setRole(role)
                        },
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        },
                        pendingApprovalsCount = pendingApprovalsCount,
                        onSyncClick = {
                            viewModel.triggerManualSync()
                        }
                    )
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (currentScreen) {
                        ErpScreen.DASHBOARD -> {
                            GeneralManagerDashboardScreen(
                                summary = centralSummary,
                                pendingPurchases = purchases,
                                recentMovements = cashMovements,
                                onNavigate = { screen -> viewModel.setScreen(screen) },
                                onApprovePurchase = { po -> viewModel.approvePurchase(po) },
                                onRejectPurchase = { po -> viewModel.rejectPurchase(po) },
                                onQuickAction = { action -> }
                            )
                        }

                        ErpScreen.CENTRAL_REPORT -> {
                            CentralFinancialReportScreen(
                                summary = centralSummary,
                                onPrintReport = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("جاري تجهيز وإرسال كشف المركز المالي الشامل إلى الطابعة الافتراضية...")
                                    }
                                }
                            )
                        }

                        ErpScreen.QUERIES -> {
                            QueriesScreen(
                                searchQuery = searchQuery,
                                onSearchChange = { q -> viewModel.setSearchQuery(q) },
                                categoryFilter = categoryFilter,
                                onCategoryChange = { cat -> viewModel.setQueryFilterCategory(cat) },
                                customers = customers,
                                workers = workers,
                                partners = partners,
                                suppliers = suppliers,
                                purchases = purchases,
                                productionRecords = productionRecords,
                                rawMaterials = rawMaterials,
                                cashMovements = cashMovements
                            )
                        }

                        ErpScreen.CUSTOMERS -> {
                            CustomersScreen(
                                customers = customers,
                                onCollectDebt = { id, amount, notes ->
                                    viewModel.collectCustomerDebt(id, amount, notes)
                                },
                                onAddCustomer = { name, phone, addr, limit, initDebt ->
                                    viewModel.addCustomer(name, phone, addr, limit, initDebt)
                                }
                            )
                        }

                        ErpScreen.WORKERS -> {
                            WorkersScreen(
                                workers = workers,
                                onWithdrawWorker = { id, amount, notes ->
                                    viewModel.withdrawWorkerAmount(id, amount, notes)
                                },
                                onAddWorker = { name, phone, job, sal, acc ->
                                    viewModel.addWorker(name, phone, job, sal, acc)
                                }
                            )
                        }

                        ErpScreen.PARTNERS -> {
                            PartnersScreen(
                                partners = partners,
                                onWithdrawPartner = { id, amount, notes ->
                                    viewModel.withdrawPartnerAmount(id, amount, notes)
                                },
                                onAddPartner = { name, phone, ratio, limit ->
                                    viewModel.addPartner(name, phone, ratio, limit)
                                }
                            )
                        }

                        ErpScreen.SUPPLIERS -> {
                            SuppliersScreen(
                                suppliers = suppliers,
                                onPaySupplier = { id, amount, notes ->
                                    viewModel.paySupplier(id, amount, notes)
                                },
                                onAddSupplier = { name, company, phone, addr, due ->
                                    viewModel.addSupplier(name, company, phone, addr, due)
                                }
                            )
                        }

                        ErpScreen.ITEMS -> {
                            ItemsScreen(
                                items = items,
                                onAddItem = { name, code, cat, unit, price, cost, stock ->
                                    viewModel.addItem(name, code, cat, unit, price, cost, stock)
                                }
                            )
                        }

                        ErpScreen.PURCHASES -> {
                            PurchasesScreen(
                                purchases = purchases,
                                currentRole = currentRole,
                                onApprovePurchase = { po -> viewModel.approvePurchase(po) },
                                onRejectPurchase = { po -> viewModel.rejectPurchase(po) },
                                onCreatePurchaseRequest = { item, qty, tot, notes ->
                                    viewModel.createPurchaseRequest(item, qty, tot, notes)
                                }
                            )
                        }

                        ErpScreen.PRODUCTION_VALUATION -> {
                            ProductionValuationScreen(
                                productionRecords = productionRecords,
                                onRecordProduction = { batch, prodVal, salesVal, qty, notes ->
                                    viewModel.recordDailyProduction(batch, prodVal, salesVal, qty, notes)
                                }
                            )
                        }

                        ErpScreen.RAW_MATERIALS_VALUATION -> {
                            RawMaterialsValuationScreen(
                                rawMaterials = rawMaterials,
                                onUpdateRawMaterial = { id, name, unit, unitPrice, totalVal, issuedVal, qty ->
                                    viewModel.updateRawMaterial(id, name, unit, unitPrice, totalVal, issuedVal, qty)
                                }
                            )
                        }

                        ErpScreen.CASHBOXES -> {
                            CashboxesScreen(
                                cashboxes = cashboxes,
                                cashMovements = cashMovements,
                                onTransferCashbox = { from, to, amount, notes ->
                                    viewModel.transferCashbox(from, to, amount, notes)
                                }
                            )
                        }

                        ErpScreen.EXPENSES -> {
                            ExpensesScreen(
                                expenses = expenses,
                                onApproveExpense = { exp -> viewModel.approveExpense(exp) },
                                onCreateExpense = { cat, desc, amt ->
                                    viewModel.createExpenseRequest(cat, desc, amt)
                                }
                            )
                        }

                        ErpScreen.IMPROVEMENTS -> {
                            ImprovementsScreen(
                                improvements = improvements,
                                onApproveImprovement = { imp -> viewModel.approveImprovement(imp) }
                            )
                        }

                        ErpScreen.USERS_ROLES -> {
                            UsersAndRolesScreen(
                                users = users,
                                onAddUser = { name, user, role, perms ->
                                    viewModel.addUser(name, user, role, perms)
                                }
                            )
                        }

                        ErpScreen.APP_STORE -> {
                            AppStoreReleasesScreen(releases = releases)
                        }

                        ErpScreen.PRINTERS -> {
                            PrintersScreen(
                                printers = printers,
                                onAddPrinter = { name, addr, paper ->
                                    viewModel.addPrinter(name, addr, paper)
                                },
                                onTestPrint = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("تمت طباعة فاتورة تجريبية بنجاح على طابعة الإدارة!")
                                    }
                                }
                            )
                        }

                        ErpScreen.AUDIT_LOGS -> {
                            AuditLogsScreen(auditLogs = auditLogs)
                        }

                        ErpScreen.OFFLINE_SYNC -> {
                            OfflineSyncScreen(
                                syncQueue = syncQueue,
                                onTriggerSync = {
                                    viewModel.triggerManualSync()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
