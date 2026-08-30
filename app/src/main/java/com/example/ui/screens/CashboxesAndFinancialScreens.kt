package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

// ==================== CASHBOXES & LEDGER SCREEN ====================

@Composable
fun CashboxesScreen(
    cashboxes: List<Cashbox>,
    cashMovements: List<CashMovement>,
    onTransferCashbox: (from: CashboxType, to: CashboxType, amount: Double, notes: String) -> Unit
) {
    var showTransferDialog by remember { mutableStateOf(false) }

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
                    text = "الخزائن ودفتر النقدية",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "دفتر الحركات النقدية الفعلية (خزينة المدير العام والخزينة المالية)",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showTransferDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("transfer_cashbox_btn")
            ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تحويل بين الخزائن", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2 Cashbox Balances Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            cashboxes.forEach { box ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (box.type == CashboxType.GENERAL_MANAGER) MaterialTheme.colorScheme.primary else Color(0xFF134E4A)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = box.name,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = if (box.type == CashboxType.GENERAL_MANAGER) Icons.Default.AdminPanelSettings else Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = ErpViewModel.formatCurrency(box.currentBalance),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (box.type == CashboxType.GENERAL_MANAGER) "السيولة بإشراف المدير العام" else "تستقبل تحصيلات المبيعات والديون",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "دفتر يومية الحركات النقدية (${cashMovements.size} حركة)",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cashMovements) { mov ->
                CashMovementItemCard(mov)
            }
        }
    }

    if (showTransferDialog) {
        var fromBox by remember { mutableStateOf(CashboxType.GENERAL_MANAGER) }
        var toBox by remember { mutableStateOf(CashboxType.FINANCIAL) }
        var amountText by remember { mutableStateOf("") }
        var notesText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showTransferDialog = false },
            title = { Text("تحويل نقدية بين الخزائن", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "التحويل من: ${fromBox.titleAr}  إلى: ${toBox.titleAr}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedButton(
                        onClick = {
                            val temp = fromBox
                            fromBox = toBox
                            toBox = temp
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.SwapVert, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("عكس اتجاه التحويل")
                    }
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("المبلغ المراد تحويله (YER)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("transfer_amount_field"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("سبب التحويل والملاحظات") },
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
                            onTransferCashbox(fromBox, toBox, amt, notesText)
                            showTransferDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("تنفيذ التحويل", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTransferDialog = false }) { Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

// ==================== EXPENSES SCREEN ====================

@Composable
fun ExpensesScreen(
    expenses: List<ExpenseRecord>,
    onApproveExpense: (ExpenseRecord) -> Unit,
    onCreateExpense: (category: String, description: String, amount: Double) -> Unit
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
                    text = "المصروفات التشغيلية",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "تسجيل واعتماد المصروفات مع الخصم من الخزينة",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_expense_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("طلب مصروف", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(expenses) { exp ->
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
                                    text = exp.expenseCategory,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = exp.description,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            Surface(
                                color = when (exp.status) {
                                    ApprovalStatus.APPROVED -> Color(0xFF16A34A).copy(alpha = 0.12f)
                                    ApprovalStatus.PENDING -> Color(0xFFD97706).copy(alpha = 0.12f)
                                    ApprovalStatus.REJECTED -> Color(0xFFDC2626).copy(alpha = 0.12f)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${exp.status.titleAr} (${ErpViewModel.formatCurrency(exp.amount)})",
                                    color = when (exp.status) {
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

                        if (exp.status == ApprovalStatus.PENDING) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { onApproveExpense(exp) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("approve_expense_btn_${exp.id}")
                            ) {
                                Text("اعتماد المصروف وخصمه من الخزينة", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var cat by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var amtText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("تسجيل طلب مصروف جديد", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cat,
                        onValueChange = { cat = it },
                        label = { Text("بند المصروف (كهرباء، وقود، صيانة، شحن...)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = amtText,
                        onValueChange = { amtText = it },
                        label = { Text("المبلغ (YER)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("exp_amount_field"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("الوصف والتفاصيل والمبرر") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val a = amtText.toDoubleOrNull() ?: 0.0
                        if (cat.isNotBlank() && a > 0) {
                            onCreateExpense(cat, desc, a)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("رفع الطلب للاعتماد", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

// ==================== IMPROVEMENTS SCREEN ====================

@Composable
fun ImprovementsScreen(
    improvements: List<ImprovementRecord>,
    onApproveImprovement: (ImprovementRecord) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "التحسينات والإصلاحات الرأسمالية",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "متابعة صيانة وتطوير خطوط الإنتاج والآليات بمصنع ليث",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(improvements) { imp ->
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
                                    text = imp.title,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${imp.description} • الكمية: ${imp.quantity}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            Surface(
                                color = when (imp.status) {
                                    ApprovalStatus.APPROVED -> Color(0xFF16A34A).copy(alpha = 0.12f)
                                    ApprovalStatus.PENDING -> Color(0xFFD97706).copy(alpha = 0.12f)
                                    ApprovalStatus.REJECTED -> Color(0xFFDC2626).copy(alpha = 0.12f)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${imp.status.titleAr} (${ErpViewModel.formatCurrency(imp.estimatedCost)})",
                                    color = when (imp.status) {
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

                        if (imp.status == ApprovalStatus.PENDING) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { onApproveImprovement(imp) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("approve_imp_btn_${imp.id}")
                            ) {
                                Text("اعتماد التحسين والإصلاح وخصمه من الخزينة", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
