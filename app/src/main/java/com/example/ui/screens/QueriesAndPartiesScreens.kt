package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.ui.viewmodels.ErpViewModel

// ==================== QUERIES SCREEN ====================

@Composable
fun QueriesScreen(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    categoryFilter: String,
    onCategoryChange: (String) -> Unit,
    customers: List<Customer>,
    workers: List<Worker>,
    partners: List<Partner>,
    suppliers: List<Supplier>,
    purchases: List<PurchaseOrder>,
    productionRecords: List<ProductionRecord>,
    rawMaterials: List<RawMaterial>,
    cashMovements: List<CashMovement>
) {
    val categories = listOf(
        "ALL" to "الكل",
        "CUSTOMERS" to "العملاء",
        "WORKERS" to "العمال",
        "PARTNERS" to "الشركاء",
        "SUPPLIERS" to "الموردون",
        "PURCHASES" to "المشتريات",
        "PRODUCTION" to "الإنتاج",
        "RAW_MATERIALS" to "المواد الخام",
        "CASH" to "حركات الخزينة"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("queries_search_input"),
            placeholder = { Text("ابحث بالاسم، الرقم، المادة أو رقم الفاتورة...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "مسح", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { (key, label) ->
                val isSelected = categoryFilter == key
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategoryChange(key) },
                    label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Results List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val q = searchQuery.trim().lowercase()

            // 1. Customers
            if (categoryFilter == "ALL" || categoryFilter == "CUSTOMERS") {
                val matched = customers.filter { it.name.lowercase().contains(q) || it.customerNumber.lowercase().contains(q) }
                if (matched.isNotEmpty()) {
                    item { QuerySectionHeader("العملاء (${matched.size})", Icons.Default.People, Color(0xFF0284C7)) }
                    items(matched) { c ->
                        QueryResultCard(
                            title = c.name,
                            subtitle = "${c.customerNumber} • ${c.phone} • ${c.address}",
                            badge = "مديونية: ${ErpViewModel.formatCurrency(c.balance)}",
                            badgeColor = if (c.balance > 0) Color(0xFFDC2626) else Color(0xFF16A34A)
                        )
                    }
                }
            }

            // 2. Workers
            if (categoryFilter == "ALL" || categoryFilter == "WORKERS") {
                val matched = workers.filter { it.name.lowercase().contains(q) || it.workerNumber.lowercase().contains(q) }
                if (matched.isNotEmpty()) {
                    item { QuerySectionHeader("العمال (${matched.size})", Icons.Default.Badge, Color(0xFFD97706)) }
                    items(matched) { w ->
                        QueryResultCard(
                            title = w.name,
                            subtitle = "${w.workerNumber} • ${w.jobTitle} • راتب: ${ErpViewModel.formatCurrency(w.basicSalary)}",
                            badge = "مستحق: ${ErpViewModel.formatCurrency(w.accruedAmount)}",
                            badgeColor = Color(0xFFD97706)
                        )
                    }
                }
            }

            // 3. Partners
            if (categoryFilter == "ALL" || categoryFilter == "PARTNERS") {
                val matched = partners.filter { it.name.lowercase().contains(q) || it.partnerNumber.lowercase().contains(q) }
                if (matched.isNotEmpty()) {
                    item { QuerySectionHeader("الشركاء (${matched.size})", Icons.Default.Handshake, Color(0xFF7C3AED)) }
                    items(matched) { p ->
                        QueryResultCard(
                            title = p.name,
                            subtitle = "نسبة: ${p.contributionRatio}% • حد شهري: ${ErpViewModel.formatCurrency(p.monthlyWithdrawalLimit)}",
                            badge = "مسحوب: ${ErpViewModel.formatCurrency(p.currentMonthWithdrawn)}",
                            badgeColor = Color(0xFF7C3AED)
                        )
                    }
                }
            }

            // 4. Suppliers
            if (categoryFilter == "ALL" || categoryFilter == "SUPPLIERS") {
                val matched = suppliers.filter { it.companyName.lowercase().contains(q) || it.name.lowercase().contains(q) }
                if (matched.isNotEmpty()) {
                    item { QuerySectionHeader("الموردون (${matched.size})", Icons.Default.LocalShipping, Color(0xFF0F766E)) }
                    items(matched) { s ->
                        QueryResultCard(
                            title = s.companyName,
                            subtitle = "مسؤول الاتصال: ${s.name} • ${s.phone}",
                            badge = "مستحق له: ${ErpViewModel.formatCurrency(s.balanceDue)}",
                            badgeColor = Color(0xFFDC2626)
                        )
                    }
                }
            }

            // 5. Purchases
            if (categoryFilter == "ALL" || categoryFilter == "PURCHASES") {
                val matched = purchases.filter { it.itemName.lowercase().contains(q) || it.orderNumber.lowercase().contains(q) }
                if (matched.isNotEmpty()) {
                    item { QuerySectionHeader("المشتريات (${matched.size})", Icons.Default.ShoppingCart, Color(0xFF0F766E)) }
                    items(matched) { po ->
                        QueryResultCard(
                            title = po.itemName,
                            subtitle = "رقم الطلب: ${po.orderNumber} • الكمية: ${po.quantity}",
                            badge = "${po.status.titleAr} (${ErpViewModel.formatCurrency(po.totalAmount)})",
                            badgeColor = when (po.status) {
                                ApprovalStatus.APPROVED -> Color(0xFF16A34A)
                                ApprovalStatus.PENDING -> Color(0xFFD97706)
                                ApprovalStatus.REJECTED -> Color(0xFFDC2626)
                            }
                        )
                    }
                }
            }

            // 6. Cash Movements
            if (categoryFilter == "ALL" || categoryFilter == "CASH") {
                val matched = cashMovements.filter { it.partyName.lowercase().contains(q) || it.movementNumber.lowercase().contains(q) }
                if (matched.isNotEmpty()) {
                    item { QuerySectionHeader("حركات الخزينة (${matched.size})", Icons.Default.AccountBalanceWallet, Color(0xFF16A34A)) }
                    items(matched) { m ->
                        QueryResultCard(
                            title = "${m.movementNumber} - ${m.partyName}",
                            subtitle = "${m.cashboxType.titleAr} • ${m.notes}",
                            badge = "${m.type.titleAr}: ${ErpViewModel.formatCurrency(m.amount)}",
                            badgeColor = Color(0xFF16A34A)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuerySectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = title, color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun QueryResultCard(title: String, subtitle: String, badge: String, badgeColor: Color) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
            Surface(
                color = badgeColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = badge,
                    color = badgeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ==================== CUSTOMERS SCREEN ====================

@Composable
fun CustomersScreen(
    customers: List<Customer>,
    onCollectDebt: (customerId: Long, amount: Double, notes: String) -> Unit,
    onAddCustomer: (name: String, phone: String, address: String, creditLimit: Double, initialDebt: Double) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCustomerForCollection by remember { mutableStateOf<Customer?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "سجل العملاء والمديونيات",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "إجمالي العملاء: ${customers.size} • إجمالي المديونيات: ${ErpViewModel.formatCurrency(customers.sumOf { it.balance })}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_customer_btn")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("إضافة عميل", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Customers List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(customers) { c ->
                val progress = if (c.creditLimit > 0) (c.balance / c.creditLimit).toFloat().coerceIn(0f, 1f) else 0f
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = c.name,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${c.customerNumber} • هاتف: ${c.phone} • ${c.address}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            Surface(
                                color = if (c.balance > 0) Color(0xFFDC2626).copy(alpha = 0.12f) else Color(0xFF16A34A).copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "المديونية: ${ErpViewModel.formatCurrency(c.balance)}",
                                    color = if (c.balance > 0) Color(0xFFDC2626) else Color(0xFF16A34A),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Credit Limit Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "الحد الائتماني الأقصى: ${ErpViewModel.formatCurrency(c.creditLimit)}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "استهلاك ${(progress * 100).toInt()}%",
                                color = if (progress > 0.8f) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = if (progress > 0.8f) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Collect Payment Trigger Button
                        Button(
                            onClick = { selectedCustomerForCollection = c },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("collect_btn_${c.id}"),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تحصيل دفعة مالية وتوريد للخزينة", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Collection Dialog
    if (selectedCustomerForCollection != null) {
        val cust = selectedCustomerForCollection!!
        var amountText by remember { mutableStateOf("") }
        var notesText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { selectedCustomerForCollection = null },
            title = {
                Text(
                    text = "سند تحصيل مالي - ${cust.name}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "المديونية الحالية: ${ErpViewModel.formatCurrency(cust.balance)}",
                        color = Color(0xFFDC2626),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "سيتم توريد المبلغ المحصل مباشرة إلى الخزينة المالية وتخفيض مديونية العميل.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("المبلغ المحصل (YER)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("collect_amount_field"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("ملاحظات السند / رقم الشيك أو الحوالة") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            onCollectDebt(cust.id, amt, notesText)
                            selectedCustomerForCollection = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("confirm_collection_btn")
                ) {
                    Text("إتمام التحصيل والتوريد", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCustomerForCollection = null }) {
                    Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Add Customer Dialog
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var limitText by remember { mutableStateOf("1000000") }
        var initialDebtText by remember { mutableStateOf("0") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("إضافة عميل جديد", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم العميل / المؤسسة") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("رقم الهاتف") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("العنوان والمدينة") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = limitText,
                        onValueChange = { limitText = it },
                        label = { Text("الحد الائتماني الأقصى (يحدد من المدير العام)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = initialDebtText,
                        onValueChange = { initialDebtText = it },
                        label = { Text("الرصيد الافتتاحي (مديونية حالية إن وجدت)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val limit = limitText.toDoubleOrNull() ?: 1000000.0
                            val debt = initialDebtText.toDoubleOrNull() ?: 0.0
                            onAddCustomer(name, phone, address, limit, debt)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("حفظ العميل", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

