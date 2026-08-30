package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ApprovalStatus
import com.example.data.models.CashMovement
import com.example.data.models.MovementType
import com.example.data.models.PurchaseOrder
import com.example.ui.components.KpiMetricCard
import com.example.ui.viewmodels.CentralFinancialSummary

import com.example.ui.viewmodels.ErpScreen
import com.example.ui.viewmodels.ErpViewModel

@Composable
fun GeneralManagerDashboardScreen(
    summary: CentralFinancialSummary,
    pendingPurchases: List<PurchaseOrder>,
    recentMovements: List<CashMovement>,
    onNavigate: (ErpScreen) -> Unit,
    onApprovePurchase: (PurchaseOrder) -> Unit,
    onRejectPurchase: (PurchaseOrder) -> Unit,
    onQuickAction: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Executive Hero Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("executive_summary_hero")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "المركز المالي الإجمالي (صافي القيمة)",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = ErpViewModel.formatCurrency(summary.netTotalWorth),
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.25f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "خزينة المدير العام", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                            Text(
                                text = ErpViewModel.formatCurrency(summary.generalCashboxBalance),
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Column {
                            Text(text = "الخزينة المالية", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                            Text(
                                text = ErpViewModel.formatCurrency(summary.financialCashboxBalance),
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Column {
                            Text(text = "إنتاج اليوم المتبقي", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                            Text(
                                text = ErpViewModel.formatCurrency(summary.remainingProductionValue),
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Pending Approvals Alert for General Manager
        val pendingList = pendingPurchases.filter { it.status == ApprovalStatus.PENDING }
        if (pendingList.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "طلبات مشتريات بانتظار اعتماد المدير العام (${pendingList.size})",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        pendingList.take(2).forEach { po ->
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp),
                                shadowElevation = 1.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = po.itemName,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "الكمية: ${po.quantity} | المبلغ: ${ErpViewModel.formatCurrency(po.totalAmount)}",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Button(
                                            onClick = { onApprovePurchase(po) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                            modifier = Modifier.testTag("approve_po_${po.id}")
                                        ) {
                                            Text("اعتماد", fontSize = 11.sp, color = Color.White)
                                        }
                                        OutlinedButton(
                                            onClick = { onRejectPurchase(po) },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text("رفض", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Navigation / Action Grid
        item {
            Text(
                text = "الإجراءات والعمليات السريعة",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    title = "التقرير الإجمالي",
                    icon = Icons.Default.Assessment,
                    color = Color(0xFF0F766E),
                    modifier = Modifier.weight(1f)
                ) { onNavigate(ErpScreen.CENTRAL_REPORT) }

                QuickActionButton(
                    title = "تحصيل عملاء",
                    icon = Icons.Default.Payments,
                    color = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                ) { onNavigate(ErpScreen.CUSTOMERS) }

                QuickActionButton(
                    title = "سحب عمال",
                    icon = Icons.Default.Badge,
                    color = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                ) { onNavigate(ErpScreen.WORKERS) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    title = "سحب شركاء",
                    icon = Icons.Default.Handshake,
                    color = Color(0xFF7C3AED),
                    modifier = Modifier.weight(1f)
                ) { onNavigate(ErpScreen.PARTNERS) }

                QuickActionButton(
                    title = "معادلة الإنتاج",
                    icon = Icons.Default.Factory,
                    color = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f)
                ) { onNavigate(ErpScreen.PRODUCTION_VALUATION) }

                QuickActionButton(
                    title = "الاستعلامات",
                    icon = Icons.Default.Search,
                    color = Color(0xFF475569),
                    modifier = Modifier.weight(1f)
                ) { onNavigate(ErpScreen.QUERIES) }
            }
        }

        // ERP Financial Metrics 2x2 Grid
        item {
            Text(
                text = "مؤشرات الأصول والالتزامات",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiMetricCard(
                    title = "قيمة المواد الخام المتبقية",
                    value = ErpViewModel.formatCurrency(summary.remainingRawMaterialsValue),
                    subtitle = "أصل مخزني بالمستودعات",
                    icon = Icons.Default.Inventory2,
                    accentColor = Color(0xFF0F766E),
                    modifier = Modifier.weight(1f)
                ) { onNavigate(ErpScreen.RAW_MATERIALS_VALUATION) }

                KpiMetricCard(
                    title = "صافي ذمم العملاء",
                    value = ErpViewModel.formatCurrency(summary.totalCustomerReceivables),
                    subtitle = "مديونيات مستحقة للشركة",
                    icon = Icons.Default.People,
                    accentColor = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f)
                ) { onNavigate(ErpScreen.CUSTOMERS) }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiMetricCard(
                    title = "مستحقات الموردين",
                    value = ErpViewModel.formatCurrency(summary.totalSupplierLiabilities),
                    subtitle = "التزامات واجبة السداد",
                    icon = Icons.Default.LocalShipping,
                    accentColor = Color(0xFFDC2626),
                    modifier = Modifier.weight(1f)
                ) { onNavigate(ErpScreen.SUPPLIERS) }

                KpiMetricCard(
                    title = "مستحقات العمال",
                    value = ErpViewModel.formatCurrency(summary.totalWorkerLiabilities),
                    subtitle = "أجور واستحقاقات جارية",
                    icon = Icons.Default.Engineering,
                    accentColor = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                ) { onNavigate(ErpScreen.WORKERS) }
            }
        }

        // Recent Cash Movement Ledger Snippet
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "دفتر يومية الخزينة الأخير",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigate(ErpScreen.CASHBOXES) }) {
                    Text("عرض الكل", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                }
            }
        }

        if (recentMovements.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("لا توجد حركات نقدية مسجلة حديثاً", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(recentMovements.take(5)) { mov ->
                CashMovementItemCard(mov)
            }
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp,
        modifier = modifier
            .height(78.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun CashMovementItemCard(mov: CashMovement) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            when (mov.type) {
                                MovementType.DEPOSIT -> Color(0xFF16A34A).copy(alpha = 0.12f)
                                MovementType.WITHDRAWAL -> Color(0xFFDC2626).copy(alpha = 0.12f)
                                MovementType.TRANSFER -> Color(0xFF0284C7).copy(alpha = 0.12f)
                                MovementType.EXPENSE -> Color(0xFFD97706).copy(alpha = 0.12f)
                                MovementType.PURCHASE -> Color(0xFF7C3AED).copy(alpha = 0.12f)
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (mov.type) {
                            MovementType.DEPOSIT -> Icons.Default.ArrowDownward
                            MovementType.WITHDRAWAL -> Icons.Default.ArrowUpward
                            MovementType.TRANSFER -> Icons.Default.SwapHoriz
                            MovementType.EXPENSE -> Icons.Default.Receipt
                            MovementType.PURCHASE -> Icons.Default.ShoppingCart
                        },
                        contentDescription = null,
                        tint = when (mov.type) {
                            MovementType.DEPOSIT -> Color(0xFF16A34A)
                            MovementType.WITHDRAWAL -> Color(0xFFDC2626)
                            MovementType.TRANSFER -> Color(0xFF0284C7)
                            MovementType.EXPENSE -> Color(0xFFD97706)
                            MovementType.PURCHASE -> Color(0xFF7C3AED)
                        },
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "${mov.partyName} (${mov.partyType})",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${mov.movementNumber} • ${mov.cashboxType.titleAr} • بواسطة ${mov.userRole}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (mov.type == MovementType.DEPOSIT) "+" else "-"}${ErpViewModel.formatCurrency(mov.amount)}",
                    color = if (mov.type == MovementType.DEPOSIT) Color(0xFF16A34A) else Color(0xFFDC2626),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = mov.type.titleAr,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }
    }
}
