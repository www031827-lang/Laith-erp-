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
import com.example.data.models.Partner
import com.example.data.models.Supplier
import com.example.data.models.Worker
import com.example.ui.viewmodels.ErpViewModel

// ==================== WORKERS SCREEN ====================

@Composable
fun WorkersScreen(
    workers: List<Worker>,
    onWithdrawWorker: (workerId: Long, amount: Double, notes: String) -> Unit,
    onAddWorker: (name: String, phone: String, jobTitle: String, basicSalary: Double, accrued: Double) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedWorkerForWithdrawal by remember { mutableStateOf<Worker?>(null) }

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
                    text = "العمال ومسحوبات الرواتب",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "إجمالي العمال: ${workers.size} • إجمالي المستحقات: ${ErpViewModel.formatCurrency(workers.sumOf { it.accruedAmount })}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_worker_btn")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("إضافة عامل", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(workers) { w ->
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
                                    text = w.name,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${w.workerNumber} • ${w.jobTitle} • هاتف: ${w.phone}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            Surface(
                                color = Color(0xFFD97706).copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "المستحق: ${ErpViewModel.formatCurrency(w.accruedAmount)}",
                                    color = Color(0xFFD97706),
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
                                text = "الراتب الأساسي: ${ErpViewModel.formatCurrency(w.basicSalary)}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "إجمالي المسحوب: ${ErpViewModel.formatCurrency(w.totalWithdrawn)}",
                                color = Color(0xFFDC2626),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { selectedWorkerForWithdrawal = w },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_worker_btn_${w.id}"),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("صرف سحب عامل (التحقق من المستحق والخصم)", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Worker Withdrawal Dialog
    if (selectedWorkerForWithdrawal != null) {
        val wrk = selectedWorkerForWithdrawal!!
        var amountText by remember { mutableStateOf("") }
        var notesText by remember { mutableStateOf("") }
        val enteredAmt = amountText.toDoubleOrNull() ?: 0.0
        val isExceeded = enteredAmt > wrk.accruedAmount

        AlertDialog(
            onDismissRequest = { selectedWorkerForWithdrawal = null },
            title = { Text("صرف سحب للعامل: ${wrk.name}", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "الرصيد المستحق الحالي: ${ErpViewModel.formatCurrency(wrk.accruedAmount)}",
                        color = Color(0xFFD97706),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("مبلغ السحب (YER)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("worker_withdraw_amount_field"),
                        isError = isExceeded,
                        singleLine = true
                    )
                    if (isExceeded) {
                        Text(
                            text = "تحذير نظامي: لا يمكن سحب مبلغ يتجاوز رصيد الاستحقاق!",
                            color = Color(0xFFDC2626),
                            fontSize = 11.sp
                        )
                    }
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("ملاحظات السحب / سبب السلفة") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (enteredAmt > 0 && !isExceeded) {
                            onWithdrawWorker(wrk.id, enteredAmt, notesText)
                            selectedWorkerForWithdrawal = null
                        }
                    },
                    enabled = enteredAmt > 0 && !isExceeded,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("تنفيذ الصرف والخصم", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedWorkerForWithdrawal = null }) { Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Add Worker Dialog
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var jobTitle by remember { mutableStateOf("") }
        var salaryText by remember { mutableStateOf("150000") }
        var accruedText by remember { mutableStateOf("50000") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("إضافة عامل جديد", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم العامل الرباعي") },
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
                        value = jobTitle,
                        onValueChange = { jobTitle = it },
                        label = { Text("المسمى الوظيفي / القسم") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = salaryText,
                        onValueChange = { salaryText = it },
                        label = { Text("الراتب الأساسي الشهري (YER)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = accruedText,
                        onValueChange = { accruedText = it },
                        label = { Text("رصيد الاستحقاق الافتتاحي (YER)") },
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
                            val sal = salaryText.toDoubleOrNull() ?: 150000.0
                            val acc = accruedText.toDoubleOrNull() ?: 0.0
                            onAddWorker(name, phone, jobTitle, sal, acc)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("حفظ العامل", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

// ==================== PARTNERS SCREEN ====================

@Composable
fun PartnersScreen(
    partners: List<Partner>,
    onWithdrawPartner: (partnerId: Long, amount: Double, notes: String) -> Unit,
    onAddPartner: (name: String, phone: String, ratio: Double, limit: Double) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedPartnerForWithdrawal by remember { mutableStateOf<Partner?>(null) }

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
                    text = "سجل الشركاء والحصص والسحوبات",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "التحقق الآلي من الحد الشهري والمتبقي المتاح للسحب",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_partner_btn")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("إضافة شريك", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(partners) { p ->
                val progress = if (p.monthlyWithdrawalLimit > 0) (p.currentMonthWithdrawn / p.monthlyWithdrawalLimit).toFloat().coerceIn(0f, 1f) else 0f
                val remainingLimit = (p.monthlyWithdrawalLimit - p.currentMonthWithdrawn).coerceAtLeast(0.0)

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
                                    text = p.name,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${p.partnerNumber} • نسبة المساهمة: ${p.contributionRatio}% • هاتف: ${p.phone}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "سقف السحب: ${ErpViewModel.formatCurrency(p.monthlyWithdrawalLimit)}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 11.sp,
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
                                text = "المسحوب هذا الشهر: ${ErpViewModel.formatCurrency(p.currentMonthWithdrawn)}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "المتبقي المتاح: ${ErpViewModel.formatCurrency(remainingLimit)}",
                                color = if (remainingLimit == 0.0) Color(0xFFDC2626) else Color(0xFF16A34A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = if (progress >= 1f) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { selectedPartnerForWithdrawal = p },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_partner_btn_${p.id}"),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("صرف سحب شريك (فحص الحد الشهري)", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Partner Withdrawal Dialog
    if (selectedPartnerForWithdrawal != null) {
        val prt = selectedPartnerForWithdrawal!!
        var amountText by remember { mutableStateOf("") }
        var notesText by remember { mutableStateOf("") }
        val enteredAmt = amountText.toDoubleOrNull() ?: 0.0
        val remainingLimit = prt.monthlyWithdrawalLimit - prt.currentMonthWithdrawn
        val isExceeded = enteredAmt > remainingLimit

        AlertDialog(
            onDismissRequest = { selectedPartnerForWithdrawal = null },
            title = { Text("صرف سحب شهري للشريك: ${prt.name}", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "الحد الشهري: ${ErpViewModel.formatCurrency(prt.monthlyWithdrawalLimit)} | المتبقي: ${ErpViewModel.formatCurrency(remainingLimit)}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("مبلغ السحب للشريك (YER)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("partner_withdraw_amount_field"),
                        isError = isExceeded,
                        singleLine = true
                    )
                    if (isExceeded) {
                        Text(
                            text = "خطأ: المبلغ المطلوب يتجاوز المتبقي من الحد الشهري للشريك!",
                            color = Color(0xFFDC2626),
                            fontSize = 11.sp
                        )
                    }
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("ملاحظات العملية") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (enteredAmt > 0 && !isExceeded) {
                            onWithdrawPartner(prt.id, enteredAmt, notesText)
                            selectedPartnerForWithdrawal = null
                        }
                    },
                    enabled = enteredAmt > 0 && !isExceeded,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("تنفيذ السحب للشريك", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPartnerForWithdrawal = null }) { Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Add Partner Dialog
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var ratioText by remember { mutableStateOf("25") }
        var limitText by remember { mutableStateOf("800000") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("إضافة شريك جديد", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم الشريك") },
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
                        value = ratioText,
                        onValueChange = { ratioText = it },
                        label = { Text("نسبة المساهمة (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = limitText,
                        onValueChange = { limitText = it },
                        label = { Text("الحد الأقصى للسحب الشهري (YER)") },
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
                            val ratio = ratioText.toDoubleOrNull() ?: 20.0
                            val limit = limitText.toDoubleOrNull() ?: 500000.0
                            onAddPartner(name, phone, ratio, limit)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("حفظ الشريك", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

// ==================== SUPPLIERS SCREEN ====================

@Composable
fun SuppliersScreen(
    suppliers: List<Supplier>,
    onPaySupplier: (supplierId: Long, amount: Double, notes: String) -> Unit,
    onAddSupplier: (name: String, company: String, phone: String, address: String, initialDue: Double) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedSupplierForPayment by remember { mutableStateOf<Supplier?>(null) }

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
                    text = "سجل الموردين والالتزامات",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "إجمالي مستحقات الموردين: ${ErpViewModel.formatCurrency(suppliers.sumOf { it.balanceDue })}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_supplier_btn")
            ) {
                Icon(Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("إضافة مورد", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(suppliers) { s ->
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
                                    text = s.companyName,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${s.supplierNumber} • مسؤول: ${s.name} • هاتف: ${s.phone}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                                Text(text = s.address, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                            }
                            Surface(
                                color = Color(0xFFDC2626).copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "المستحق: ${ErpViewModel.formatCurrency(s.balanceDue)}",
                                    color = Color(0xFFDC2626),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { selectedSupplierForPayment = s },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pay_supplier_btn_${s.id}"),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("سداد دفعة للمورد وتخفيض الالتزام", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Supplier Payment Dialog
    if (selectedSupplierForPayment != null) {
        val sup = selectedSupplierForPayment!!
        var amountText by remember { mutableStateOf("") }
        var notesText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { selectedSupplierForPayment = null },
            title = { Text("سداد مستحقات: ${sup.companyName}", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "الرصيد المستحق للمورد: ${ErpViewModel.formatCurrency(sup.balanceDue)}",
                        color = Color(0xFFDC2626),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("المبلغ المسدد (YER)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("supplier_pay_amount_field"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("رقم السند / الشيك / الحوالة") },
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
                            onPaySupplier(sup.id, amt, notesText)
                            selectedSupplierForPayment = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("إتمام السداد والخصم", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedSupplierForPayment = null }) { Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Add Supplier Dialog
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var company by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var dueText by remember { mutableStateOf("0") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("إضافة مورد جديد", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = company,
                        onValueChange = { company = it },
                        label = { Text("اسم الشركة أو المؤسسة الموردة") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم مسؤول الاتصال والمبيعات") },
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
                        label = { Text("العنوان / المدينة") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = dueText,
                        onValueChange = { dueText = it },
                        label = { Text("الرصيد الافتتاحي (مستحقات سابقة)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (company.isNotBlank()) {
                            val due = dueText.toDoubleOrNull() ?: 0.0
                            onAddSupplier(name, company, phone, address, due)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("حفظ المورد", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

