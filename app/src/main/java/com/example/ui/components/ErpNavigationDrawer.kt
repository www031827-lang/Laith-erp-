package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodels.ErpScreen

data class NavItem(
    val screen: ErpScreen,
    val icon: ImageVector,
    val category: String,
    val badge: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErpNavigationDrawerContent(
    currentScreen: ErpScreen,
    onScreenSelect: (ErpScreen) -> Unit,
    pendingApprovalsCount: Int,
    onDismiss: () -> Unit
) {
    val items = listOf(
        // الإدارة والتقارير
        NavItem(ErpScreen.DASHBOARD, Icons.Default.Dashboard, "الإدارة والتقارير"),
        NavItem(ErpScreen.CENTRAL_REPORT, Icons.Default.Assessment, "الإدارة والتقارير"),
        NavItem(ErpScreen.QUERIES, Icons.Default.Search, "الإدارة والتقارير"),

        // الحسابات والأطراف
        NavItem(ErpScreen.CUSTOMERS, Icons.Default.People, "الحسابات والأطراف"),
        NavItem(ErpScreen.WORKERS, Icons.Default.Badge, "الحسابات والأطراف"),
        NavItem(ErpScreen.PARTNERS, Icons.Default.Handshake, "الحسابات والأطراف"),
        NavItem(ErpScreen.SUPPLIERS, Icons.Default.LocalShipping, "الحسابات والأطراف"),

        // العمليات والمخزون
        NavItem(ErpScreen.ITEMS, Icons.Default.Category, "العمليات والمخزون"),
        NavItem(
            ErpScreen.PURCHASES,
            Icons.Default.ShoppingCart,
            "العمليات والمخزون",
            if (pendingApprovalsCount > 0) "$pendingApprovalsCount" else null
        ),
        NavItem(ErpScreen.PRODUCTION_VALUATION, Icons.Default.Factory, "العمليات والمخزون"),
        NavItem(ErpScreen.RAW_MATERIALS_VALUATION, Icons.Default.Inventory, "العمليات والمخزون"),

        // المالية والخزائن
        NavItem(ErpScreen.CASHBOXES, Icons.Default.AccountBalanceWallet, "المالية والخزائن"),
        NavItem(ErpScreen.EXPENSES, Icons.Default.ReceiptLong, "المالية والخزائن"),
        NavItem(ErpScreen.IMPROVEMENTS, Icons.Default.Build, "المالية والخزائن"),

        // الأمان والنظام
        NavItem(ErpScreen.USERS_ROLES, Icons.Default.ManageAccounts, "الأمان والنظام"),
        NavItem(ErpScreen.APP_STORE, Icons.Default.Shop, "الأمان والنظام"),
        NavItem(ErpScreen.PRINTERS, Icons.Default.Print, "الأمان والنظام"),
        NavItem(ErpScreen.AUDIT_LOGS, Icons.Default.Shield, "الأمان والنظام"),
        NavItem(ErpScreen.OFFLINE_SYNC, Icons.Default.CloudSync, "الأمان والنظام")
    )

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.width(320.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CorporateFare,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "نظام ليث ERP",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "الهيكل الإداري والمحاسبي المتكامل",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 8.dp))

            // Navigation items grouped
            val grouped = items.groupBy { it.category }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                grouped.forEach { (category, navList) ->
                    item {
                        Text(
                            text = category,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp, start = 8.dp)
                        )
                    }
                    items(navList.size) { idx ->
                        val item = navList[idx]
                        val isSelected = currentScreen == item.screen
                        Surface(
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("nav_item_${item.screen.name.lowercase()}")
                                .clickable {
                                    onScreenSelect(item.screen)
                                    onDismiss()
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = item.screen.titleAr,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                                if (item.badge != null) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.error,
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = item.badge,
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 8.dp))

            // Footer Aden Server info
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "مرجع التوقيت الرسمي: Asia/Aden (UTC+3)",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "حماية مشددة: HTTPS / RBAC / Audit Active",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}
