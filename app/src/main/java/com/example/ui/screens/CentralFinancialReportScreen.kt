package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodels.CentralFinancialSummary
import com.example.ui.viewmodels.ErpViewModel

@Composable
fun CentralFinancialReportScreen(
    summary: CentralFinancialSummary,
    onPrintReport: () -> Unit
) {
    val totalAssets = summary.generalCashboxBalance +
            summary.financialCashboxBalance +
            summary.remainingProductionValue +
            summary.remainingRawMaterialsValue +
            summary.totalCustomerReceivables

    val totalLiabilities = summary.totalSupplierLiabilities + summary.totalWorkerLiabilities

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Print Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "التقرير المالي الإجمالي المركزي",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "كشف المركز المالي الشامل الموحد لمجموعة ليث ERP",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
                Button(
                    onClick = onPrintReport,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("print_report_btn")
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("طباعة الكشف", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        // Summary Net Worth Display Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("net_worth_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "صافي القيمة الإجمالية للمشروع (Net Asset Value)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = ErpViewModel.formatCurrency(summary.netTotalWorth),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "إجمالي الأصول والموجودات (+)", color = Color(0xFF16A34A), fontSize = 11.sp)
                            Text(
                                text = ErpViewModel.formatCurrency(totalAssets),
                                color = Color(0xFF16A34A),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(text = "إجمالي الالتزامات المستحقة (-)", color = Color(0xFFDC2626), fontSize = 11.sp)
                            Text(
                                text = ErpViewModel.formatCurrency(totalLiabilities),
                                color = Color(0xFFDC2626),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // The Exact Mathematical ERP Formula Breakdown Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Functions,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "المعادلة المحاسبية المركزية المعتمدة",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "صافي القيمة = خزينة المدير العام + الخزينة المالية + إنتاج اليوم المتبقي + المواد الخام المتبقية + صافي العملاء - مستحقات الموردين - مستحقات العمال",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Detailed Assets Breakdown
        item {
            Text(
                text = "1. تفاصيل الأصول والموجودات النقدية والمخزنية (+)",
                color = Color(0xFF16A34A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            FinancialRowItem(
                title = "خزينة المدير العام (نقد فعلي)",
                category = "نقدية وسيولة",
                amount = summary.generalCashboxBalance,
                isPositive = true,
                note = "المبالغ الموردة بيد المدير العام"
            )
        }

        item {
            FinancialRowItem(
                title = "الخزينة المالية (نقد فعلي)",
                category = "نقدية وسيولة",
                amount = summary.financialCashboxBalance,
                isPositive = true,
                note = "خاصة بالمدير المالي وتستقبل تحصيلات العملاء"
            )
        }

        item {
            FinancialRowItem(
                title = "المتبقي من إنتاج اليوم",
                category = "مخزون إنتاج",
                amount = summary.remainingProductionValue,
                isPositive = true,
                note = "قيمة إنتاج اليوم - قيمة مبيعات اليوم"
            )
        }

        item {
            FinancialRowItem(
                title = "قيمة المواد الخام المتبقية",
                category = "مخزون مواد خام",
                amount = summary.remainingRawMaterialsValue,
                isPositive = true,
                note = "قيمة المواد الخام - المواد الصادرة"
            )
        }

        item {
            FinancialRowItem(
                title = "صافي حسابات العملاء (الذمم)",
                category = "ذمم مدينة",
                amount = summary.totalCustomerReceivables,
                isPositive = true,
                note = "مديونيات مستحقة على العملاء لصالح الشركة"
            )
        }

        // Detailed Liabilities Breakdown
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "2. تفاصيل الالتزامات والمستحقات الواجبة (-)",
                color = Color(0xFFDC2626),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            FinancialRowItem(
                title = "مستحقات الموردين (التزامات)",
                category = "ذمم دائنة",
                amount = summary.totalSupplierLiabilities,
                isPositive = false,
                note = "فواتير وأرصدة آجلة للموردين"
            )
        }

        item {
            FinancialRowItem(
                title = "مستحقات العمال (أجور واستحقاقات)",
                category = "التزامات موارد بشرية",
                amount = summary.totalWorkerLiabilities,
                isPositive = false,
                note = "رواتب ومستحقات غير مسحوبة"
            )
        }

        // Accounting Policy Rule Alert (Rule #44)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "قاعدة محاسبية تنظيمية صارمة",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "هناك فرق دقيق بين الخزينة (أموال نقدية فعلية)، حساب العميل (ذمة مالية)، حساب المورد (التزام)، حساب العامل (مستحق)، وقيمة المواد الخام والإنتاج (أصول مخزنية). لذلك لا يتم خلطها عشوائياً في دفتر الخزينة، بل تفرد الخزائن بدفتر حركات مستقل، ويكون هذا التقرير تحليلياً شاملاً للمركز المالي.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FinancialRowItem(
    title: String,
    category: String,
    amount: Double,
    isPositive: Boolean,
    note: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (isPositive) Color(0xFF16A34A).copy(alpha = 0.12f) else Color(0xFFDC2626).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (isPositive) "(+)" else "(-)",
                            color = if (isPositive) Color(0xFF16A34A) else Color(0xFFDC2626),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = note,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = ErpViewModel.formatCurrency(amount),
                    color = if (isPositive) Color(0xFF16A34A) else Color(0xFFDC2626),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = category,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }
    }
}

