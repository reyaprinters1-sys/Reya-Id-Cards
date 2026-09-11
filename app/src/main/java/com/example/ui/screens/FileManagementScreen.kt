package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.OrderEntity
import com.example.ui.components.LanguageThemeQuickSwitchers
import com.example.ui.components.ReyaLogoIcon
import com.example.ui.components.ReyaWhatsAppChatButton
import com.example.ui.components.SopFileDetailsDialog
import com.example.util.AppSettingsManager
import com.example.util.tr
import com.example.viewmodel.CardProViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagementScreen(
    viewModel: CardProViewModel
) {
    val currentLang by AppSettingsManager.language.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    var selectedOrderForDetails by remember { mutableStateOf<OrderEntity?>(null) }

    // Group orders by Date string (yyyy-MM-dd) then by Dealer
    val groupedOrders = remember(orders) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        orders.groupBy { dateFormat.format(Date(it.createdAt)) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReyaLogoIcon(size = 34.dp)
                        Column {
                            Text(
                                text = tr("Reya ID Cards - கோப்புகள்", "Reya ID Cards - Files", currentLang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = tr("கணினி போல்டர் படிநிலை & ஃபைல் கட்டமைப்பு", "Folder Structure & File Storage SOP", currentLang),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    LanguageThemeQuickSwitchers(compact = true)

                    ReyaWhatsAppChatButton(compact = true)
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
        ) {
            // SOP Rule Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF047857),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = "Google Drive Connected",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Google Drive இணைப்பு செயலில் உள்ளது",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFA7F3D0)
                                )
                            }
                            Text(
                                text = "கணக்கு: reyaidcards@gmail.com\nஅனைத்து SOP ஆர்டர் போல்டர்களும் Google Drive-ல் தானாகப் பராமரிக்கப்படுகின்றன.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD1FAE5),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // SOP Rule Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "1. தனித்துவமான ஆர்டர் ஐடி ஃபார்முலா:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "வடிவம்: [DEALER_ID]-[DATE]-[SERIAL_NO]\nஉதாரணம்: DLR01-09SEP-001 (Reya Printers | 09 செப் | 001)",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFFBBF24),
                                modifier = Modifier.padding(8.dp),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Visual Directory Tree
            item {
                Text(
                    text = "2. கணினி போல்டர் படிநிலை (Folder Directory Tree):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ID_CARD_ORDERS_2026 /",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }

                        if (groupedOrders.isEmpty()) {
                            Text(
                                text = "    └── (ஆர்டர்கள் எதுவும் இன்னும் சேமிக்கப்படவில்லை)",
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        } else {
                            groupedOrders.forEach { (dateKey, dateOrders) ->
                                Text(
                                    text = "    └── ${dateKey}_ORDERS /",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                val dealerGroups = dateOrders.groupBy { it.dealerCode }
                                dealerGroups.forEach { (dealerCode, dOrders) ->
                                    val dealerNameClean = dOrders.firstOrNull()?.dealerName?.replace(" ", "_") ?: "Dealer"
                                    Text(
                                        text = "         ├── ${dealerCode}_${dealerNameClean} /",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF34D399),
                                        fontSize = 11.sp
                                    )

                                    dOrders.forEach { ord ->
                                        val custClean = ord.customerName.replace(" ", "_")
                                        Text(
                                            text = "         │    ├── ${ord.orderCode}_${custClean}_Details.txt",
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFFE2E8F0),
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = "         │    ├── ${ord.orderCode}_Photo.jpg",
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = "         │    └── ${ord.orderCode}_Payment.jpg",
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Order Files List (Tap to inspect .txt)
            item {
                Text(
                    text = "ஆர்டர் கோப்புகள் பார்வை (Tap to view & copy Details.txt):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            items(orders) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { selectedOrderForDetails = order },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Article,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "${order.orderCode}_${order.customerName.replace(" ", "_")}_Details.txt",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "டீலர்: ${order.dealerName} • ${order.quantity} கார்டுகள் • ${order.status}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    selectedOrderForDetails?.let { order ->
        SopFileDetailsDialog(
            order = order,
            fileContent = viewModel.getOrderDetailsFileContent(order),
            onDismiss = { selectedOrderForDetails = null }
        )
    }
}
