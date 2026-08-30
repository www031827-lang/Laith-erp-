package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

// ==================== ITEMS SCREEN ====================

@Composable
fun ItemsScreen(
    items: List<Item>,
    onAddItem: (name: String, code: String, category: String, unit: String, price: Double, cost: Double, stock: Double) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "دليل الأصناف وقائمة الأسعار",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "إجمالي الأصناف: ${items.size} • مراقبة حد الطلب والمخزون",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_item_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("إضافة صنف", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items) { itm ->
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
                                    text = itm.name,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${itm.itemCode} • ${itm.category} • الوحدة: ${itm.unit}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "سعر البيع: ${ErpViewModel.formatCurrency(itm.unitPrice)}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "تكلفة الإنتاج: ${ErpViewModel.formatCurrency(itm.costPrice)}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "الرصيد المخزني: ${itm.currentStock} ${itm.unit}",
                                color = if (itm.currentStock <= itm.minStockAlert) Color(0xFFDC2626) else Color(0xFF16A34A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var cat by remember { mutableStateOf("منتجات تامة الصنع") }
        var unit by remember { mutableStateOf("كرتون") }
        var priceText by remember { mutableStateOf("15000") }
        var costText by remember { mutableStateOf("10000") }
        var stockText by remember { mutableStateOf("100") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("إضافة صنف جديد", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم الصنف والعيار") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("كود الصنف (اختياري)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = cat,
                        onValueChange = { cat = it },
                        label = { Text("التصنيف الرئيسي") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("الوحدة القياسية") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("سعر البيع المعتمد (YER)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = costText,
                        onValueChange = { costText = it },
                        label = { Text("سعر التكلفة التقديرية (YER)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it },
                        label = { Text("الكمية الافتتاحية بالمستودع") },
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
                            val p = priceText.toDoubleOrNull() ?: 0.0
                            val c = costText.toDoubleOrNull() ?: 0.0
                            val s = stockText.toDoubleOrNull() ?: 0.0
                            onAddItem(name, code, cat, unit, p, c, s)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("حفظ الصنف", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

// ==================== PURCHASES SCREEN ====================

@Composable
fun PurchasesScreen(
    purchases: List<PurchaseOrder>,
    currentRole: AppRole,
    onApprovePurchase: (PurchaseOrder) -> Unit,
    onRejectPurchase: (PurchaseOrder) -> Unit,
    onCreatePurchaseRequest: (itemName: String, quantity: Double, totalAmount: Double, notes: String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "المشتريات ودورة الاعتمادات",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "طلبات الشراء، الاعتماد، والخصم المالي الآلي",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("create_purchase_btn")
            ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("طلب شراء جديد", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(purchases) { po ->
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = po.itemName,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${po.orderNumber} • الكمية: ${po.quantity} • بواسطة: ${po.createdByRole}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                                if (po.notes.isNotBlank()) {
                                    Text(text = "ملاحظات: ${po.notes}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                }
                            }
                            Surface(
                                color = when (po.status) {
                                    ApprovalStatus.APPROVED -> Color(0xFF16A34A).copy(alpha = 0.12f)
                                    ApprovalStatus.PENDING -> Color(0xFFD97706).copy(alpha = 0.12f)
                                    ApprovalStatus.REJECTED -> Color(0xFFDC2626).copy(alpha = 0.12f)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${po.status.titleAr} (${ErpViewModel.formatCurrency(po.totalAmount)})",
                                    color = when (po.status) {
                                        ApprovalStatus.APPROVED -> Color(0xFF16A34A)
                                        ApprovalStatus.PENDING -> Color(0xFFD97706)
                                        ApprovalStatus.REJECTED -> Color(0xFFDC2626)
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // If PENDING, show Approval Action Bar for General Manager
                        if (po.status == ApprovalStatus.PENDING) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onApprovePurchase(po) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).testTag("approve_purchase_btn_${po.id}")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("اعتماد وخصم من الخزينة", fontSize = 11.sp, color = Color.White)
                                }
                                OutlinedButton(
                                    onClick = { onRejectPurchase(po) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(0.5f)
                                ) {
                                    Text("رفض", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        var item by remember { mutableStateOf("") }
        var qtyText by remember { mutableStateOf("1") }
        var totalText by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("طلب مشتريات جديد", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "يتم رفع طلب المشتريات من المدير المالي أو مسؤولي الأقسام إلى المدير العام للاعتماد والخصم.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    OutlinedTextField(
                        value = item,
                        onValueChange = { item = it },
                        label = { Text("بيان المادة أو الصنف المطلوب") },
                        modifier = Modifier.fillMaxWidth().testTag("po_item_field"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = qtyText,
                        onValueChange = { qtyText = it },
                        label = { Text("الكمية") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = totalText,
                        onValueChange = { totalText = it },
                        label = { Text("التكلفة الإجمالية التقديرية (YER)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("po_amount_field"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("سبب الشراء والتبرير") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = qtyText.toDoubleOrNull() ?: 1.0
                        val tot = totalText.toDoubleOrNull() ?: 0.0
                        if (item.isNotBlank() && tot > 0) {
                            onCreatePurchaseRequest(item, qty, tot, notes)
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("إرسال الطلب للاعتماد", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

// ==================== PRODUCTION VALUATION SCREEN ====================

@Composable
fun ProductionValuationScreen(
    productionRecords: List<ProductionRecord>,
    onRecordProduction: (batch: String, prodVal: Double, salesVal: Double, qty: Double, notes: String) -> Unit
) {
    var showRecordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "قيمة الإنتاج والمخزون اليومي",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "معادلة: إنتاج اليوم - مبيعات اليوم = المتبقي",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = { showRecordDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_production_batch_btn")
            ) {
                Icon(Icons.Default.PrecisionManufacturing, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تسجيل وردية إنتاج", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Equation Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "المعادلة المحاسبية لحساب قيمة إنتاج اليوم المتبقي",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "قيمة إنتاج اليوم المتبقي = قيمة إنتاج اليوم - قيمة مبيعات اليوم",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "يمثل هذا الرقم قيمة البضاعة تامة الصنع التي دخلت المستودع ولم تبع حتى لحظة التقرير.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(productionRecords) { pr ->
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
                                    text = "تشغيلة رقم: ${pr.batchNumber}",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "التاريخ: ${pr.dateString} • الكمية: ${pr.outputQuantity} وحدة", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "المتبقي: ${ErpViewModel.formatCurrency(pr.remainingProductionValue)}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "قيمة الإنتاج: ${ErpViewModel.formatCurrency(pr.dailyProductionValue)}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "قيمة المبيعات: ${ErpViewModel.formatCurrency(pr.dailySalesValue)}",
                                color = Color(0xFFD97706),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (pr.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "ملاحظات: ${pr.notes}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }

    if (showRecordDialog) {
        var batch by remember { mutableStateOf("BATCH-260827-03") }
        var prodValText by remember { mutableStateOf("1600000") }
        var salesValText by remember { mutableStateOf("950000") }
        var qtyText by remember { mutableStateOf("160") }
        var notes by remember { mutableStateOf("وردية إنتاج مسائية") }

        AlertDialog(
            onDismissRequest = { showRecordDialog = false },
            title = { Text("تسجيل وردية إنتاج وحساب المعادلة", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = batch,
                        onValueChange = { batch = it },
                        label = { Text("رقم التشغيلة / الوردية") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = prodValText,
                        onValueChange = { prodValText = it },
                        label = { Text("قيمة إنتاج اليوم (YER)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("prod_val_field"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = salesValText,
                        onValueChange = { salesValText = it },
                        label = { Text("قيمة مبيعات اليوم (YER)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("sales_val_field"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = qtyText,
                        onValueChange = { qtyText = it },
                        label = { Text("إجمالي عدد الوحدات المنتجة") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات الجودة والتشغيل") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = prodValText.toDoubleOrNull() ?: 0.0
                        val s = salesValText.toDoubleOrNull() ?: 0.0
                        val q = qtyText.toDoubleOrNull() ?: 0.0
                        onRecordProduction(batch, p, s, q, notes)
                        showRecordDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("حساب وحفظ السجل", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRecordDialog = false }) { Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

// ==================== RAW MATERIALS VALUATION SCREEN ====================

@Composable
fun RawMaterialsValuationScreen(
    rawMaterials: List<RawMaterial>,
    onUpdateRawMaterial: (id: Long, name: String, unit: String, unitPrice: Double, totalVal: Double, issuedVal: Double, qty: Double) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedForEdit by remember { mutableStateOf<RawMaterial?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "قيمة المواد الخام والمخزون",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "معادلة: قيمة المواد الخام - الصادر = المتبقي",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = {
                    selectedForEdit = null
                    showDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_raw_material_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("إضافة مادة خام", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Equation Formula Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "المعادلة المحاسبية لقيمة المواد الخام المتبقية بالمستودع",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "قيمة المواد الخام المتبقية = إجمالي قيمة المواد الخام - قيمة المواد الصادرة",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "إجمالي قيمة المواد الخام المتبقية حالياً: ${ErpViewModel.formatCurrency(rawMaterials.sumOf { it.remainingStockValue })}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(rawMaterials) { rm ->
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
                                    text = rm.name,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${rm.materialCode} • الكمية: ${rm.currentQuantity} ${rm.unit} • سعر الوحدة: ${ErpViewModel.formatCurrency(rm.unitPrice)}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "المتبقي: ${ErpViewModel.formatCurrency(rm.remainingStockValue)}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "إجمالي القيمة: ${ErpViewModel.formatCurrency(rm.totalStockValue)}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "الصادر للتشغيل: ${ErpViewModel.formatCurrency(rm.issuedValue)}",
                                color = Color(0xFFDC2626),
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                selectedForEdit = rm
                                showDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تحديث الحركة والصادر", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        val editing = selectedForEdit
        var name by remember { mutableStateOf(editing?.name ?: "") }
        var unit by remember { mutableStateOf(editing?.unit ?: "طن") }
        var unitPriceText by remember { mutableStateOf(editing?.unitPrice?.toString() ?: "450000") }
        var totalValText by remember { mutableStateOf(editing?.totalStockValue?.toString() ?: "3600000") }
        var issuedValText by remember { mutableStateOf(editing?.issuedValue?.toString() ?: "1200000") }
        var qtyText by remember { mutableStateOf(editing?.currentQuantity?.toString() ?: "5.0") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = if (editing != null) "تحديث حركة المادة الخام" else "إضافة مادة خام جديدة",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم المادة الخام") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("الوحدة (طن / برميل / رزمة)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = totalValText,
                        onValueChange = { totalValText = it },
                        label = { Text("إجمالي قيمة المادة الخام (YER)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("raw_total_val_field"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = issuedValText,
                        onValueChange = { issuedValText = it },
                        label = { Text("قيمة المواد الصادرة لخطوط الإنتاج (YER)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("raw_issued_val_field"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = qtyText,
                        onValueChange = { qtyText = it },
                        label = { Text("الكمية الفعلية بالمستودع") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val up = unitPriceText.toDoubleOrNull() ?: 0.0
                        val tv = totalValText.toDoubleOrNull() ?: 0.0
                        val iv = issuedValText.toDoubleOrNull() ?: 0.0
                        val q = qtyText.toDoubleOrNull() ?: 0.0
                        onUpdateRawMaterial(editing?.id ?: 0L, name, unit, up, tv, iv, q)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("حفظ وتطبيق المعادلة", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}
